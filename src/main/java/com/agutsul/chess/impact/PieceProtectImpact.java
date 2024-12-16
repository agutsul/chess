package com.agutsul.chess.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceProtectImpact<COLOR extends Color,
                                PIECE1 extends Piece<COLOR> & Capturable,
                                PIECE2 extends Piece<COLOR>>
        extends AbstractTargetImpact<PIECE1, PIECE2> {

    public PieceProtectImpact(PIECE1 source, PIECE2 target) {
        super(Type.PROTECT, source, target);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getSource(), getTarget());
    }

    @Override
    public Position getPosition() {
        return getTarget().getPosition();
    }
}