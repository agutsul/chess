package com.agutsul.chess.game;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;
import com.agutsul.chess.game.observer.GameTimeoutTerminationObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.winner.GameTimeoutWinnerEvaluator;

public final class TimeoutGame
        extends AbstractGame {

    private static final Logger LOGGER = getLogger(TimeoutGame.class);

    private final Game game;
    private final long timeout;

    public TimeoutGame(Game game, long timeoutMillis) {
        super(LOGGER, game.getWhitePlayer(), game.getBlackPlayer());

        this.game = game;
        this.timeout = timeoutMillis;

        ((Observable) this.game).addObserver(new GameTimeoutTerminationObserver());
    }

    @Override
    public void run() {
        var playableGame = (AbstractPlayableGame) this.game;
        try {
            if (this.timeout <= 0) {
                throw new GameTimeoutException("Game timeout");
            }

            execute();
        } catch (GameTimeoutException e) {
            playableGame.notifyObservers(new GameTimeoutTerminationEvent(playableGame));

            playableGame.evaluateWinner(new GameTimeoutWinnerEvaluator());
            playableGame.notifyObservers(new GameOverEvent(playableGame));

            LOGGER.info("Game over ( game timeout ): {}", e.getMessage());
        } catch (Throwable throwable) {
            LOGGER.error("{}: Game exception, board state '{}': {}",
                    getCurrentPlayer().getColor(),
                    getBoard().getState(),
                    getStackTrace(throwable)
            );

            playableGame.notifyObservers(new GameExceptionEvent(playableGame, throwable));
            playableGame.notifyObservers(new GameOverEvent(playableGame));
        }
    }

    private void execute() throws InterruptedException, ExecutionException {
        var executor = newSingleThreadExecutor();
        try {
            var future = executor.submit(this.game);
            try {
                future.get(this.timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                throw new GameTimeoutException("Game timeout");
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
    }

    @Override
    public Player getCurrentPlayer() {
        return this.game.getCurrentPlayer();
    }

    @Override
    public Player getOpponentPlayer() {
        return this.game.getOpponentPlayer();
    }

    @Override
    public Board getBoard() {
        return this.game.getBoard();
    }

    @Override
    public Journal<ActionMemento<?, ?>> getJournal() {
        return this.game.getJournal();
    }

    @Override
    public GameContext getContext() {
        return this.game.getContext();
    }
}