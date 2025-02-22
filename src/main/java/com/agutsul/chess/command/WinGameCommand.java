package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.INSUFFICIENT_MATERIAL;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedWinBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.WinExecutionEvent;
import com.agutsul.chess.activity.action.event.WinPerformedEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class WinGameCommand
        extends AbstractUpdateBoardStateCommand {

    private static final Logger LOGGER = getLogger(WinGameCommand.class);

    public WinGameCommand(Game game, Player player) {
        super(LOGGER, game, player);
    }

    @Override
    protected void updateBoardState() {
        var board = ((AbstractPlayableGame) this.game).getBoard();

        validate(board);

        board.setState(agreedWinBoardState(board, player.getColor()));
    }

    @Override
    protected Event createPreExecutionEvent() {
        return new WinExecutionEvent(this.player);
    }

    @Override
    protected Event createPostExecutionEvent() {
        return new WinPerformedEvent(this.player);
    }

    private void validate(Board board) {
        var currentState = board.getState();
        if (currentState.isAnyType(CHECKED, INSUFFICIENT_MATERIAL)
                || currentState.isTerminal()) {

            throw new IllegalActionException(String.format(
                    "%s: Unable to win while being in '%s' state",
                    player.getColor(),
                    currentState
            ));
        }

        var boardStates = new ArrayList<>(board.getStates());
        if (boardStates.size() < 2) {
            throw new IllegalActionException(String.format(
                    "%s: Unable to win with unknown opponent's state",
                    player.getColor()
            ));
        }

        // check if opponent is unable to checkmate by insufficient material
        var opponentState = boardStates.get(boardStates.size() - 2);
        if (!opponentState.isType(INSUFFICIENT_MATERIAL)) {
            throw new IllegalActionException(String.format(
                    "%s: Unable to win with '%s' opponent's state",
                    player.getColor(),
                    opponentState
            ));
        }
    }
}