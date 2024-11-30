package com.agutsul.chess.action.memento;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PromoteActionMemento
        extends AbstractActionMemento<String, ActionMemento<String,String>> {

    private Piece.Type pieceType;

    PromoteActionMemento(Action.Type actionType,
                         ActionMemento<String, String> origin) {

        super(now(), actionType, origin.getSource(), origin);
    }

    @Override
    public Color getColor() {
        return getTarget().getColor();
    }

    public void setPieceType(Piece.Type pieceType) {
        this.pieceType = pieceType;
    }

    @Override
    public Piece.Type getPieceType() {
        return pieceType;
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s)",
                getActionType(),
                getTarget(),
                pieceType != null ? pieceType.name() : "?"
        );
    }
}