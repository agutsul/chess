package com.agutsul.chess.activity.action.memento;

import java.time.LocalDateTime;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class ActionMementoDecorator<SOURCE,TARGET>
        implements ActionMemento<SOURCE,TARGET> {

    private final ActionMemento<?,?> origin;
    private final String code;

    public ActionMementoDecorator(ActionMemento<?,?> origin, String code) {
        this.origin = origin;
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return origin.getCreatedAt();
    }

    @Override
    public Color getColor() {
        return origin.getColor();
    }

    @Override
    public Action.Type getActionType() {
        return origin.getActionType();
    }

    @Override
    public Piece.Type getPieceType() {
        return origin.getPieceType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SOURCE getSource() {
        return (SOURCE) origin.getSource();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TARGET getTarget() {
        return (TARGET) origin.getTarget();
    }

    @Override
    public String toString() {
        return origin.toString();
    }
}