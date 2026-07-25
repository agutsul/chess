package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceMonitorImpact<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Capturable & Lineable>
        extends AbstractTargetActivity<Impact.Type,PIECE,Position>
        implements Impact<PIECE> {

    private final ImpactValueProvider valueProvider;

    public PieceMonitorImpact(PIECE piece, Position position) {
        super(Impact.Type.MONITOR, piece, position);
        this.valueProvider = new ImpactValueProvider(() -> calculateValue());
    }

    @Override
    public final Integer getValue() {
        return this.valueProvider.get();
    }

    @Override
    public final String toString() {
        return String.format("%s:%s[%s]", getType(), getSource(), getPosition());
    }

    @Override
    public final Position getPosition() {
        return getTarget();
    }

    private Integer calculateValue() {
        return getSource().getDirection();
    }
}