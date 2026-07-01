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

    private Integer value;

    public PieceBackwardImpact(PIECE piece) {
        super(Impact.Type.BACKWARD, piece);
    }

    @Override
    public final Integer getValue() {
        if (this.value != null) {
            return this.value;
        }

        this.value = calculateValue();
        return this.value;
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