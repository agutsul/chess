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

    public PieceBackwardImpact(PIECE piece) {
        super(Impact.Type.BACKWARD, piece);
    }

    @Override
    public final String toString() {
        return String.format("*%s", getSource());
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }
}