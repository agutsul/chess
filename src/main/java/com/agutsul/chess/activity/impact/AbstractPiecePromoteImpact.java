package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Promotable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPiecePromoteImpact<COLOR  extends Color,
                                          PIECE  extends Piece<COLOR> & Promotable,
                                          SOURCE extends AbstractSourceActivity<Impact.Type,PIECE> & Impact<PIECE>>
        extends AbstractTargetActivity<Impact.Type,AbstractSourceActivity<Impact.Type,PIECE>,Piece.Type>
        implements PiecePromoteImpact<COLOR,PIECE> {

    AbstractPiecePromoteImpact(SOURCE sourceImpact, Piece.Type pieceType) {
        super(Impact.Type.PROMOTE, sourceImpact, pieceType);
    }

    @Override
    public final Integer getValue() {
        return PiecePromoteImpact.super.getValue() * Math.abs(getSource().getValue()) + getPieceType().rank();
    }

    @Override
    public final PIECE getPiece() {
        return getSource().getSource();
    }

    @Override
    public final Piece.Type getPieceType() {
        return getTarget();
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final SOURCE getSource() {
        return (SOURCE) super.getSource();
    }

    @Override
    public final String toString() {
        return String.format("(%s)=%s", getSource(), getPieceType());
    }
}