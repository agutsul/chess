package com.agutsul.chess.activity.impact;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

import java.util.function.Supplier;

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

    private final Supplier<Integer> valueProvider;

    AbstractPiecePromoteImpact(SOURCE sourceImpact, Piece.Type pieceType) {
        super(Impact.Type.PROMOTE, sourceImpact, pieceType);
        this.valueProvider = new ImpactValueSupplier<>(this::calculateValue, INTEGER_ZERO);
    }

    @Override
    public final Integer getValue() {
        return this.valueProvider.get();
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
        return String.format("%s:(%s)=%s", getType(), getSource(), getPieceType());
    }

    private Integer calculateValue() {
        var value = (int) Math.pow(2, getPieceType().rank());
        return getSource().getValue() + getPiece().getDirection() * value;
    }
}