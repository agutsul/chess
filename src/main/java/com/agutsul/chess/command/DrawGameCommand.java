package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDrawBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.DrawExecutionEvent;
import com.agutsul.chess.activity.action.event.DrawPerformedEvent;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public class DrawGameCommand
        extends AbstractCommand {

    private static final Logger LOGGER = getLogger(DrawGameCommand.class);

    private final Game game;
    private final Player player;

    public DrawGameCommand(Game game, Player player) {
        super(LOGGER);
        this.game = game;
        this.player = player;
    }

    @Override
    protected void executeInternal() throws CommandException {
        ((Observable) this.game).notifyObservers(new DrawExecutionEvent(this.player));

        try {
            var board = ((AbstractPlayableGame) this.game).getBoard();
            board.setState(agreedDrawBoardState(board, player.getColor()));
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }

        ((Observable) this.game).notifyObservers(new DrawPerformedEvent(this.player));
    }
}