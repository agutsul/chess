package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceOverloadingImpact<COLOR extends Color,
                                    PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractTargetActivity<Impact.Type,PIECE,Position>
        implements Impact<PIECE> {

    public PieceOverloadingImpact(PIECE piece, Position position) {
        super(Impact.Type.OVERLOADING, piece, position);
    }

    @Override
    public final String toString() {
        return String.format("%s over %s", getSource(), getPosition());
    }

    @Override
    public final Position getPosition() {
        return getTarget();
    }
}