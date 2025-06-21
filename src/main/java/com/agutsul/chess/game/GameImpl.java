package com.agutsul.chess.game;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.exception.ActionTimeoutException;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.winner.ActionTimeoutWinnerEvaluator;
import com.agutsul.chess.rule.winner.WinnerEvaluatorImpl;

class GameImpl extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(GameImpl.class);

    GameImpl(Player whitePlayer, Player blackPlayer,
             Board board, Journal<ActionMemento<?,?>> journal,
             BoardStateEvaluator<BoardState> boardStateEvaluator,
             GameContext context) {

        super(LOGGER, whitePlayer, blackPlayer, board, journal, boardStateEvaluator, context);
    }

    @Override
    public void run() {
        notifyObservers(new GameStartedEvent(this));
        logger.info("Game started ...");

        try {
            execute();

            setWinnerPlayer(evaluateWinner(new WinnerEvaluatorImpl()));
            notifyObservers(new GameOverEvent(this));

            logger.info("Game over");
        } catch (ActionTimeoutException e) {
            notifyObservers(new PlayerTerminateActionEvent(getCurrentPlayer(), Type.TIMEOUT));

            setWinnerPlayer(evaluateWinner(new ActionTimeoutWinnerEvaluator()));
            notifyObservers(new GameOverEvent(this));

            logger.info("Game over ( action timeout ): {}", e.getMessage());
        } catch (GameInterruptionException e) {
            logger.info("Game interrupted ( game timeout ): {}", e.getMessage());
        } catch (Throwable throwable) {
            logger.error("{}: Game exception, board state '{}': {}",
                    getCurrentPlayer().getColor(), getBoard().getState(),
                    getStackTrace(throwable)
            );

            notifyObservers(new GameExceptionEvent(this, throwable));
            notifyObservers(new GameOverEvent(this));
        }
    }
}