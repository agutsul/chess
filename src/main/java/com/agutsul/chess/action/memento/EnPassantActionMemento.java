package com.agutsul.chess.action.memento;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

final class EnPassantActionMemento
        implements ActionMemento<ActionMemento<String,String>,String> {

    private final Action.Type actionType;
    private final ActionMemento<String,String> origin;
    private final Position position;

    EnPassantActionMemento(Action.Type actionType,
                           ActionMemento<String,String> origin,
                           Position position) {
        this.actionType = actionType;
        this.origin = origin;
        this.position = position;
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
        return origin.getPieceType();
    }

    @Override
    public ActionMemento<String,String> getSource() {
        return origin;
    }

    @Override
    public String getTarget() {
        return String.valueOf(position);
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s)", getActionType(), getSource(), getTarget());
    }
}
