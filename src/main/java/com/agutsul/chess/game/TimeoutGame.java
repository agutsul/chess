package com.agutsul.chess.game;

import static com.agutsul.chess.rule.winner.WinnerEvaluator.Type.GAME_TIMEOUT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;
import com.agutsul.chess.game.event.GameWinnerEvent;
import com.agutsul.chess.rule.winner.WinnerEvaluator;
import com.agutsul.chess.timeout.Timeout.Type;

class TimeoutGame<GAME extends Game & Observable>
        extends AbstractGameProxy<GAME>
        implements Playable {

    private static final Logger LOGGER = getLogger(TimeoutGame.class);

    private final long timeoutMillis;
    private final boolean evaluateWinner;

    TimeoutGame(GAME game, long timeoutMillis) {
        this(game, timeoutMillis, true);
    }

    TimeoutGame(GAME game, long timeoutMillis, boolean evaluateWinner) {
        super(game);
        this.timeoutMillis = timeoutMillis;
        this.evaluateWinner = evaluateWinner;
    }

    @Override
    public void run() {
        // There is valid case when game timeout is zero but game still should be executed.
        // So, in such case potential GameTimeoutException can be thrown and it should be intercepted below
        var tGame = this.timeoutMillis != 0
                ? new TimeoutGameImpl<>(this.game, this.timeoutMillis, this.evaluateWinner)
                : this.game;

        try {
            tGame.run();
        } catch (GameTimeoutException e) {
            LOGGER.info("Game over ( game timeout ): {}", e.getMessage());

            notifyObservers(new GameTimeoutTerminationEvent(this.game, e.getPlayer()));
            notifyObservers(new GameWinnerEvent(this.game, e.getPlayer(), GAME_TIMEOUT));
            notifyObservers(new GameOverEvent(this.game));
        } catch (Throwable throwable) {
            LOGGER.error("{}: Game exception, board state '{}': {}",
                    getCurrentPlayer().getColor(), getBoard().getState(),
                    getStackTrace(throwable)
            );

            notifyObservers(new GameExceptionEvent(this.game, throwable));
            notifyObservers(new GameOverEvent(this.game));
        }
    }

    private static final class TimeoutGameImpl<G extends Game & Observable>
            extends AbstractGameProxy<G>
            implements Playable {

        private static final String GAME_TIMEOUT_MESSAGE = "Game timeout exceeded";
        private static final String GAME_TIMEOUT_INVALID_MESSAGE = "Game timeout: invalid timeout value";

        private static final int POOL_SIZE = 1;

        private final long timeout;
        private final boolean evaluateWinner;

        TimeoutGameImpl(G game, long timeout, boolean evaluateWinner) {
            super(game);

            this.timeout = timeout;
            this.evaluateWinner = evaluateWinner;
        }

        @Override
        public void run() {
            if (this.timeout < 0) {
                throw new GameTimeoutException(
                        this.game.getCurrentPlayer(),
                        GAME_TIMEOUT_INVALID_MESSAGE
                );
            }

            try (var executor = newThreadExecutor(POOL_SIZE)) {
                var future = executor.submit(this.game);
                try {
                    future.get(this.timeout, MILLISECONDS);
                } catch (TimeoutException e) {
                    try {
                        timeout();
                    } finally {
                        future.cancel(true);
                    }
                } catch (ExecutionException e) {
                    // re-throw origin exception. It is expected to be GameTimeoutException
                    throw e.getCause();
                } catch (InterruptedException e) {
                    throw new GameInterruptionException("Timeout game interrupted");
                }
            } catch (GameTimeoutException | GameInterruptionException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private void timeout() {
            var context = this.game.getContext();
            var isMixedTimeout = Stream.of(context.getTimeout())
                    .flatMap(Optional::stream)
                    .anyMatch(timeout -> timeout.isType(Type.ACTIONS_PER_PERIOD));

            if (!isMixedTimeout) {
                throw new GameTimeoutException(
                        this.game.getCurrentPlayer(),
                        GAME_TIMEOUT_MESSAGE
                );
            }

            var journal = this.game.getJournal();
            if (journal.isEmpty()) {
                throw new GameTimeoutException(
                        this.game.getCurrentPlayer(),
                        String.format("%s: no actions performed", GAME_TIMEOUT_MESSAGE)
                );
            }

            var expectedActions = Stream.of(context.getExpectedActions())
                    .flatMap(Optional::stream)
                    .findFirst()
                    .orElse(0);

            if (journal.size() < expectedActions) {
                throw new GameTimeoutException(
                        this.game.getCurrentPlayer(),
                        String.format("%s and actual actions '%d' less than expected actions '%d'",
                                GAME_TIMEOUT_MESSAGE, journal.size(), expectedActions
                        )
                );
            }

            if (this.evaluateWinner) {
                notifyObservers(new GameWinnerEvent(this.game, WinnerEvaluator.Type.STANDARD));
                notifyObservers(new GameOverEvent(this.game));
            }
        }

        private static ExecutorService newThreadExecutor(int poolSize) {
            return new ThreadPoolExecutor(poolSize, poolSize, 0L, MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    BasicThreadFactory.builder()
                        .namingPattern("TimeoutGameExecutorThread-%d")
                        .priority(Thread.MAX_PRIORITY)
                        .build()
            );
        }
    }
}