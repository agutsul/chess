package com.agutsul.chess.action.memento;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

class ActionMementoProxy<SOURCE,TARGET>
        implements ActionMemento<SOURCE,TARGET> {

    private final ActionMemento<SOURCE,TARGET> origin;

    ActionMementoProxy(ActionMemento<SOURCE,TARGET> origin) {
        this.origin = origin;
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
    public SOURCE getSource() {
        return origin.getSource();
    }

    @Override
    public TARGET getTarget() {
        return origin.getTarget();
    }

    @Override
    public String toString() {
        return origin.toString();
    }
}