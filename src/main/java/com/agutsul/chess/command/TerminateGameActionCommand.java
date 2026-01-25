package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDefeatBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedDrawBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedWinBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.exitedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.timeoutBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.player.Player;

public class TerminateGameActionCommand
        extends AbstractCommand {

    private static final Logger LOGGER = getLogger(TerminateGameActionCommand.class);

    private static final String UNKNOWN_TERMINATION_TYPE_ERROR_MESSAGE = "Unknown termination type";

    private final Game game;
    private final Player player;
    private final Type type;

    public TerminateGameActionCommand(Game game, Player player, Type type) {
        super(LOGGER);

        this.game = game;
        this.player = player;
        this.type = type;
    }

    @Override
    protected final void executeInternal() throws CommandException {
        notifyGameObservers(new ActionTerminationEvent(this.player, this.type));

        try {
            var board = this.game.getBoard();
            board.setState(createBoardState(board, this.player.getColor(), this.type));
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }

        notifyGameObservers(new ActionTerminatedEvent(this.player, this.type));
    }

    private void notifyGameObservers(Event event) {
        ((Observable) this.game).notifyObservers(event);
    }

    private static BoardState createBoardState(Board board, Color color, Type type) {
        return switch (type) {
        case DEFEAT  -> agreedDefeatBoardState(board, color);
        case DRAW    -> agreedDrawBoardState(board, color);
        case WIN     -> agreedWinBoardState(board, color);
        case EXIT    -> exitedBoardState(board, color);
        case TIMEOUT -> timeoutBoardState(board, color);
        default      -> throw new IllegalStateException(String.format(
                "%s: %s",
                UNKNOWN_TERMINATION_TYPE_ERROR_MESSAGE,
                type
        ));
        };
    }
}