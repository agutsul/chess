package com.agutsul.chess.command;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.WinExecutionEvent;
import com.agutsul.chess.activity.action.event.WinPerformedEvent;
import com.agutsul.chess.board.state.AgreedWinBoardState;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public class WinGameCommand
        extends AbstractCommand {

    private static final Logger LOGGER = getLogger(WinGameCommand.class);

    private final Game game;
    private final Player player;

    public WinGameCommand(Game game, Player player) {
        super(LOGGER);
        this.game = game;
        this.player = player;
    }

    @Override
    protected void executeInternal() throws CommandException {
        ((Observable) this.game).notifyObservers(new WinExecutionEvent(this.player));

        try {
            var board = ((AbstractPlayableGame) this.game).getBoard();
            board.setState(new AgreedWinBoardState(board, player.getColor()));
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }

        ((Observable) this.game).notifyObservers(new WinPerformedEvent(this.player));
    }
}