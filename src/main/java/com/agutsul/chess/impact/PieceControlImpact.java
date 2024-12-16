package com.agutsul.chess.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceControlImpact<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Capturable>
        extends AbstractTargetActivity<PIECE,Position>
        implements Impact<PIECE> {

    public PieceControlImpact(PIECE piece, Position position) {
        super(Impact.Type.CONTROL, piece, position);
    }

    @Override
    public String toString() {
        return String.format("%sX%s", getSource(), getPosition());
    }

    @Override
    public Position getPosition() {
        return getTarget();
    }
}