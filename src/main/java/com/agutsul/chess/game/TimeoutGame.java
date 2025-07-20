package com.agutsul.chess.game;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.event.Observable;
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

            var executor = newSingleThreadExecutor();
            try {
                var future = executor.submit(this.game);
                try {
                    future.get(this.timeout, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    processTimeout();
                }
            } finally {
                try {
                    executor.shutdown();
                    if (!executor.awaitTermination(1, TimeUnit.MILLISECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                }
            }
        } catch (GameTimeoutException e) {
            LOGGER.info("Game over ( game timeout ): {}", e.getMessage());

            notifyObservers(new GameTimeoutTerminationEvent(this.game));
            notifyObservers(new GameWinnerEvent(WinnerEvaluator.Type.GAME_TIMEOUT));
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

    void processTimeout() {
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

        var expectedActions = Stream.of(context.getTotalActions())
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

    void evaluateWinner() {
        notifyObservers(new GameWinnerEvent(WinnerEvaluator.Type.STANDARD));
        notifyObservers(new GameOverEvent(this.game));
    }
}