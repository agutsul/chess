package com.agutsul.chess.action.memento;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

class ActionMementoImpl<SOURCE,TARGET>
        extends AbstractActionMemento<SOURCE,TARGET> {

    private final Color color;
    private final Piece.Type pieceType;

    protected ActionMementoImpl(Color color,
                                Action.Type actionType,
                                Piece.Type pieceType,
                                SOURCE source,
                                TARGET target) {

        super(now(), actionType, source, target);

        this.color = color;
        this.pieceType = pieceType;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Piece.Type getPieceType() {
        return pieceType;
    }

    @Override
    public String toString() {
        return String.format("%s %s(%s %s)", getActionType(),
                    pieceType.name(), getSource(), getTarget());
    }
}