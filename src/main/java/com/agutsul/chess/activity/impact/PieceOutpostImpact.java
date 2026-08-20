package com.agutsul.chess.activity.impact;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

import java.util.function.Supplier;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceOutpostImpact<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractTargetActivity<Impact.Type,PIECE,Position>
        implements Impact<PIECE> {

    private final Supplier<Integer> valueProvider;

    public PieceOutpostImpact(PIECE piece, Position position) {
        super(Impact.Type.OUTPOST, piece, position);
        this.valueProvider = new ImpactValueProvider<>(this::calculateValue, INTEGER_ZERO);
    }

    @Override
    public final Integer getValue() {
        return this.valueProvider.get();
    }

    @Override
    public final String toString() {
        return String.format("%s:%s ~ %s", getType(), getSource(), getPosition());
    }

    @Override
    public final Position getPosition() {
        return getTarget();
    }

    private Integer calculateValue() {
        return getSource().getValue();
    }
}