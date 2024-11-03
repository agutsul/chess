package com.agutsul.chess.action.memento;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PromoteActionMemento
        implements ActionMemento<String, ActionMemento<String,String>> {

    private final Action.Type actionType;
    private final Piece.Type pieceType;
    private final ActionMemento<String,String> origin;

    public PromoteActionMemento(Action.Type actionType,
                                Piece.Type pieceType,
                                ActionMemento<String, String> origin) {
        this.actionType = actionType;
        this.pieceType = pieceType;
        this.origin = origin;
    }

    @Override
    public Color getColor() {
        return origin.getColor();
    }

    @Override
    public Action.Type getActionType() {
        return actionType;
    }

    @Override
    public Piece.Type getPieceType() {
        return pieceType;
    }

    @Override
    public String getSource() {
        return origin.getSource();
    }

    @Override
    public ActionMemento<String,String> getTarget() {
        return origin;
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s)", actionType, getTarget(), pieceType.name());
    }
}