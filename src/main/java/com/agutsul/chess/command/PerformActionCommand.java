package com.agutsul.chess.command;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.event.ActionExecutionEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.action.memento.ActionMementoFactory;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PerformActionCommand
        extends AbstractCommand {

    private static final Logger LOGGER = getLogger(PerformActionCommand.class);

    private static final String MISSED_PIECE_MESSAGE = "Missed piece on position";
    private static final String MISSED_POSITION_MESSAGE = "Missed position";

    private final Board board;
    private final Observable observable;

    private Piece<Color> sourcePiece;
    private Position targetPosition;

    private Action<?> action;
    private ActionMemento<?,?> memento;

    public PerformActionCommand(Board board, Observable observable) {
        super(LOGGER);
        this.board = board;
        this.observable = observable;
    }

    public void setSource(String source) {
        var piece = board.getPiece(source);
        if (piece.isEmpty()) {
            throw new IllegalPositionException(
                    String.format("%s: %s", MISSED_PIECE_MESSAGE, source)
            );
        }

        this.sourcePiece = piece.get();
    }

    public void setTarget(String target) {
        var position = board.getPosition(target);
        if (position.isEmpty()) {
            throw new IllegalPositionException(
                    String.format("%s: %s", MISSED_POSITION_MESSAGE, target)
            );
        }

        this.targetPosition = position.get();
    }

    @Override
    protected void preExecute() throws CommandException {
        var targetAction = board.getActions(this.sourcePiece).stream()
                .filter(action -> Objects.equals(action.getPosition(), this.targetPosition))
                .findFirst();

        if (targetAction.isEmpty()) {
            throw new IllegalActionException(
                    String.format("Invalid action for %s at '%s' and position '%s'",
                            lowerCase(this.sourcePiece.getType().name()),
                            this.sourcePiece.getPosition(),
                            this.targetPosition
                    )
            );
        }

        this.action = targetAction.get();
        this.memento = createMemento(this.action);
    }

    @Override
    protected void executeInternal() throws CommandException {
        this.observable.notifyObservers(new ActionExecutionEvent(this.action));

        try {
            this.action.execute();
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }

        this.observable.notifyObservers(new ActionPerformedEvent(this.memento));
    }

    private static ActionMemento<?,?> createMemento(Action<?> action) {
        return ActionMementoFactory.create(action);
    }
}