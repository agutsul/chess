package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.exitedBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.ExitExecutionEvent;
import com.agutsul.chess.activity.action.event.ExitPerformedEvent;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class ExitGameCommand
        extends AbstractUpdateBoardStateCommand {

    private static final Logger LOGGER = getLogger(ExitGameCommand.class);

    public ExitGameCommand(Game game, Player player) {
        super(LOGGER, game, player);
    }

    @Override
    protected void updateBoardState() {
        var board = ((AbstractPlayableGame) this.game).getBoard();
        board.setState(exitedBoardState(board, player.getColor()));
    }

    @Override
    protected Event createPreExecutionEvent() {
        return new ExitExecutionEvent(this.player);
    }

    @Override
    protected Event createPostExecutionEvent() {
        return new ExitPerformedEvent(this.player);
    }
}