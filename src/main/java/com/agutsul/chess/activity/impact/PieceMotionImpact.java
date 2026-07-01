package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceMotionImpact<COLOR extends Color,
                               PIECE extends Piece<COLOR> & Movable>
        extends AbstractTargetActivity<Impact.Type,PIECE,Position>
        implements Impact<PIECE> {

    private Integer value;

    public PieceMotionImpact(PIECE piece, Position position) {
        super(Impact.Type.MOTION, piece, position);
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
    public final Position getPosition() {
        return getTarget();
    }

    @Override
    public final String toString() {
        return String.format("%s:%s->%s", getType(), getSource(), getPosition());
    }

    private Integer calculateValue() {
        return getSource().getDirection();
    }
}