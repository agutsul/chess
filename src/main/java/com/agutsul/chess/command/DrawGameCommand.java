package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDrawBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.DrawExecutionEvent;
import com.agutsul.chess.activity.action.event.DrawPerformedEvent;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class DrawGameCommand
        extends AbstractUpdateBoardStateCommand {

    private static final Logger LOGGER = getLogger(DrawGameCommand.class);

    public DrawGameCommand(Game game, Player player) {
        super(LOGGER, game, player);
    }

    @Override
    protected void updateBoardState() {
        var board = ((AbstractPlayableGame) this.game).getBoard();
        board.setState(agreedDrawBoardState(board, player.getColor()));
    }

    @Override
    protected Event createPreExecutionEvent() {
        return new DrawExecutionEvent(this.player);
    }

    @Override
    protected Event createPostExecutionEvent() {
        return new DrawPerformedEvent(this.player);
    }
}