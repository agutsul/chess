package com.agutsul.chess.command;

import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.event.ActionExecutionEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.action.memento.ActionMementoFactory;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PerformActionCommand
        extends AbstractCommand {

    private static final ActionMementoFactory MEMENTO_FACTORY = ActionMementoFactory.INSTANCE;

    private final Board board;
    private final Observable observable;

    private Piece<Color> sourcePiece;
    private Position targetPosition;

    private Action<?> action;
    private ActionMemento memento;

    public PerformActionCommand(Board board, Observable observable) {
        this.board = board;
        this.observable = observable;
    }

    public void setSource(String source) {
        var piece = board.getPiece(source);
        if (piece.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Missed piece on position: %s", source));
        }

        this.sourcePiece = piece.get();
    }

    public void setTarget(String target) {
        var position = board.getPosition(target);
        if (position.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Missed position: %s", target));
        }

        this.targetPosition = position.get();
    }

    @Override
    protected void preExecute() {
        var targetAction = board.getActions(sourcePiece).stream()
                .filter(action -> Objects.equals(action.getPosition(), this.targetPosition))
                .findFirst();

        if (targetAction.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Invalid action for position: %s", this.targetPosition));
        }

        this.action = targetAction.get();
        this.memento = MEMENTO_FACTORY.create(this.action);
    }

    @Override
    protected void executeInternal() {
        this.observable.notifyObservers(new ActionExecutionEvent(this.action));
        this.action.execute();
    }

    @Override
    protected void postExecute() {
        observable.notifyObservers(new ActionPerformedEvent(this.memento));
    }
}