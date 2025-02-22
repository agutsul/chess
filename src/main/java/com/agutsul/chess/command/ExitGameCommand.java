package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.exitedBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.ExitExecutionEvent;
import com.agutsul.chess.activity.action.event.ExitPerformedEvent;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public class ExitGameCommand
        extends AbstractCommand {

    private static final Logger LOGGER = getLogger(ExitGameCommand.class);

    private final Game game;
    private final Player player;

    public ExitGameCommand(Game game, Player player) {
        super(LOGGER);
        this.game = game;
        this.player = player;
    }

    @Override
    protected void executeInternal() throws CommandException {
        ((Observable) this.game).notifyObservers(new ExitExecutionEvent(this.player));

        try {
            var board = ((AbstractPlayableGame) this.game).getBoard();
            board.setState(exitedBoardState(board, player.getColor()));
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }

        ((Observable) this.game).notifyObservers(new ExitPerformedEvent(this.player));
    }
}