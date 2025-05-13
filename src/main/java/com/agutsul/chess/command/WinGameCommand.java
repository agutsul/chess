package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedWinBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.WinExecutionEvent;
import com.agutsul.chess.activity.action.event.WinPerformedEvent;
import com.agutsul.chess.event.Event;
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
}