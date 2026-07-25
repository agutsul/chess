package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Backwardable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceBackwardImpact<COLOR extends Color,
                                 PIECE extends Piece<COLOR> & Backwardable>
        extends AbstractSourceActivity<Impact.Type,PIECE>
        implements Impact<PIECE> {

    private final ImpactValueProvider valueProvider;

    public PieceBackwardImpact(PIECE piece) {
        super(Impact.Type.BACKWARD, piece);
        this.valueProvider = new ImpactValueProvider(() -> calculateValue());
    }

    @Override
    public final Integer getValue() {
        return this.valueProvider.get();
    }

    @Override
    public final String toString() {
        return String.format("%s:*%s", getType(), getSource());
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }

    private Integer calculateValue() {
        return getSource().getValue();
    }
}