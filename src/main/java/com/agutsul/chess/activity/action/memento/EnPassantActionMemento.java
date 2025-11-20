package com.agutsul.chess.activity.action.memento;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

final class EnPassantActionMemento
        extends AbstractActionMemento<ActionMemento<String,String>,String> {

    EnPassantActionMemento(Action.Type actionType,
                           ActionMemento<String,String> origin,
                           Position position) {

        super(now(), actionType, origin, String.valueOf(position));
    }

    @Override
    public Color getColor() {
        return getSource().getColor();
    }

    @Override
    public Piece.Type getPieceType() {
        return getSource().getPieceType();
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s)", getActionType(), getSource(), getTarget());
    }
}