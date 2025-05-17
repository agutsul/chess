package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDefeatBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedDrawBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedWinBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.exitedBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.player.Player;

public class TerminateGameActionCommand
        extends AbstractUpdateBoardStateCommand {

    private static final Logger LOGGER = getLogger(TerminateGameActionCommand.class);

    private final Type type;

    public TerminateGameActionCommand(Game game, Player player, Type type) {
        super(LOGGER, game, player);
        this.type = type;
    }

    @Override
    protected void updateBoardState() {
        var board = this.game.getBoard();
        board.setState(createBoardState(board, this.player.getColor(), this.type));
    }

    @Override
    protected Event createPreExecutionEvent() {
        return new ActionTerminationEvent(this.player, this.type);
    }

    @Override
    protected Event createPostExecutionEvent() {
        return new ActionTerminatedEvent(this.player, this.type);
    }

    private static BoardState createBoardState(Board board, Color color, Type type) {
        switch (type) {
        case DEFEAT:
            return agreedDefeatBoardState(board, color);
        case DRAW:
            return agreedDrawBoardState(board, color);
        case WIN:
            return agreedWinBoardState(board, color);
        case EXIT:
            return exitedBoardState(board, color);
        default:
            throw new IllegalStateException(String.format("Unknown termination type: %s", type));
        }
    }
}