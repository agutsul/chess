package com.agutsul.chess.game;

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

    private static final String GAME_TIMEOUT_MESSAGE = "Game timeout exceeded";

    private final long timeout;

    TimeoutGame(GAME game, long timeoutMillis) {
        super(game);
        this.timeout = timeoutMillis;
    }

    @Override
    public void run() {
        try {
            if (this.timeout <= 0) {
                throw new GameTimeoutException("Game timeout: invalid timeout value");
            }

            try (var executor = newSingleThreadExecutor()) {
                var future = executor.submit(this.game);
                try {
                    future.get(this.timeout, MILLISECONDS);
                } catch (TimeoutException e) {
                    try {
                        processTimeout();
                    } finally {
                        future.cancel(true);
                    }
                } catch (ExecutionException e) {
                    // re-throw origin exception. It is expected to be GameTimeoutException
                    throw e.getCause();
                } catch (InterruptedException e) {
                    throw new GameInterruptionException("Timeout game interrupted");
                }
            }
        } catch (GameTimeoutException e) {
            LOGGER.info("Game over ( game timeout ): {}", e.getMessage());

            notifyObservers(new GameTimeoutTerminationEvent(this.game));
            notifyObservers(new GameWinnerEvent(this.game, WinnerEvaluator.Type.GAME_TIMEOUT));
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

    protected void processTimeout() {
        var context = this.game.getContext();
        var isMixedTimeout = Stream.of(context.getTimeout())
                .flatMap(Optional::stream)
                .anyMatch(timeout -> timeout.isType(Type.ACTIONS_PER_PERIOD));

        if (!isMixedTimeout) {
            throw new GameTimeoutException(GAME_TIMEOUT_MESSAGE);
        }

        var journal = this.game.getJournal();
        if (journal.isEmpty()) {
            throw new GameTimeoutException(String.format(
                    "%s: no actions performed",
                    GAME_TIMEOUT_MESSAGE
            ));
        }

        var expectedActions = Stream.of(context.getExpectedActions())
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(0);

        if (journal.size() < expectedActions) {
            throw new GameTimeoutException(String.format(
                    "%s and actual actions '%d' less than expected actions '%d'",
                    GAME_TIMEOUT_MESSAGE, journal.size(), expectedActions
            ));
        }

        evaluateWinner();
    }

    protected void evaluateWinner() {
        notifyObservers(new GameWinnerEvent(this.game, WinnerEvaluator.Type.STANDARD));
        notifyObservers(new GameOverEvent(this.game));
    }

    private static ExecutorService newSingleThreadExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                BasicThreadFactory.builder()
                    .namingPattern("TimeoutGameExecutorThread-%d")
                    .priority(Thread.MAX_PRIORITY)
                    .build()
        );
    }
}