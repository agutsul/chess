package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Isolatable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceIsolationImpact<COLOR extends Color,
                                  PIECE extends Piece<COLOR> & Isolatable>
        extends AbstractSourceActivity<Impact.Type,PIECE>
        implements Impact<PIECE> {

    private Integer value;

    public PieceIsolationImpact(PIECE piece) {
        super(Impact.Type.ISOLATION, piece);
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
        return String.format("%s:|%s|", getType(), getSource());
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }

    private Integer calculateValue() {
        return Math.negateExact(getSource().getValue());
    }
}