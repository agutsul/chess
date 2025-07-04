package com.agutsul.chess.game;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;

import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;
import com.agutsul.chess.game.observer.GameTimeoutTerminationObserver;
import com.agutsul.chess.rule.winner.GameTimeoutWinnerEvaluator;

final class TimeoutGame
        extends AbstractGameProxy<AbstractPlayableGame>
        implements Playable {

    private static final Logger LOGGER = getLogger(TimeoutGame.class);

    private final long timeout;

    TimeoutGame(AbstractPlayableGame game, long timeoutMillis) {
        super(game);
        this.timeout = timeoutMillis;

        addObserver(new GameTimeoutTerminationObserver());
    }

    @Override
    public void run() {
        var executor = newSingleThreadExecutor();
        try {
            if (this.timeout <= 0) {
                throw new GameTimeoutException("Game timeout: invalid timeout value");
            }

            var future = executor.submit(this.game);
            try {
                future.get(this.timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                throw new GameTimeoutException("Game timeout exceeded");
            }
        } catch (GameTimeoutException e) {
            LOGGER.info("Game over ( game timeout ): {}", e.getMessage());

            notifyObservers(new GameTimeoutTerminationEvent(this.game));
            this.game.setWinnerPlayer(this.game.evaluateWinner(new GameTimeoutWinnerEvaluator()));

            notifyObservers(new GameOverEvent(this.game));
        } catch (Throwable throwable) {
            LOGGER.error("{}: Game exception, board state '{}': {}",
                    getCurrentPlayer().getColor(), getBoard().getState(),
                    getStackTrace(throwable)
            );

            notifyObservers(new GameExceptionEvent(this.game, throwable));
            notifyObservers(new GameOverEvent(this.game));
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
    }
}