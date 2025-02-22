package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDefeatBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.DefeatExecutionEvent;
import com.agutsul.chess.activity.action.event.DefeatPerformedEvent;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class DefeatGameCommand
        extends AbstractUpdateBoardStateCommand {

    private static final Logger LOGGER = getLogger(DefeatGameCommand.class);

    public DefeatGameCommand(Game game, Player player) {
        super(LOGGER, game, player);
    }

    @Override
    protected void updateBoardState() {
        var board = ((AbstractPlayableGame) this.game).getBoard();
        board.setState(agreedDefeatBoardState(board, player.getColor()));
    }

    @Override
    protected Event createPreExecutionEvent() {
        return new DefeatExecutionEvent(this.player);
    }

    @Override
    protected Event createPostExecutionEvent() {
        return new DefeatPerformedEvent(this.player);
    }
}