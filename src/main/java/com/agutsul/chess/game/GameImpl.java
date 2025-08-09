package com.agutsul.chess.game;

import static com.agutsul.chess.rule.winner.WinnerEvaluator.Type.ACTION_TIMEOUT;
import static com.agutsul.chess.rule.winner.WinnerEvaluator.Type.STANDARD;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.exception.ActionTimeoutException;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.game.event.GameWinnerEvent;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.rule.board.BoardStateEvaluator;

class GameImpl extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(GameImpl.class);

    GameImpl(Player whitePlayer, Player blackPlayer,
             Board board, Journal<ActionMemento<?,?>> journal,
             BoardStateEvaluator<BoardState> boardStateEvaluator,
             GameContext context) {

        super(LOGGER, whitePlayer, blackPlayer,
                board, journal, boardStateEvaluator, context
        );
    }

    @Override
    public void run() {
        notifyObservers(new GameStartedEvent(this));
        logger.info("Game started ...");

        try {
            execute();

            notifyObservers(new GameWinnerEvent(this, STANDARD));
            notifyObservers(new GameOverEvent(this));

            logger.info("Game over");
        } catch (ActionTimeoutException e) {
            logger.info("Game over ( action timeout ): {}", e.getMessage());

            notifyObservers(new PlayerTerminateActionEvent(e.getPlayer(), Type.TIMEOUT));
            notifyObservers(new GameWinnerEvent(this, e.getPlayer(), ACTION_TIMEOUT));
            notifyObservers(new GameOverEvent(this));
        } catch (GameInterruptionException e) {
            logger.warn("Game interrupted ( game timeout ): {}", e.getMessage());
        } catch (GameTimeoutException e) {
            throw e;
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