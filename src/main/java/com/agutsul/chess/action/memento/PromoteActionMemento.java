package com.agutsul.chess.action.memento;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

final class PromoteActionMemento
        extends AbstractActionMemento<String, ActionMemento<String,String>> {

    private final Piece.Type pieceType;

    PromoteActionMemento(Action.Type actionType,
                         Piece.Type pieceType,
                         ActionMemento<String, String> origin) {

        super(now(), actionType, origin.getSource(), origin);
        this.pieceType = pieceType;
    }

    @Override
    public Color getColor() {
        return getTarget().getColor();
    }

    @Override
    public Piece.Type getPieceType() {
        return pieceType;
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s)", getActionType(), getTarget(), pieceType.name());
    }
}