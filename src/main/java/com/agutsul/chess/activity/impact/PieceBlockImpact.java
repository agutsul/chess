package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Blockable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceBlockImpact<COLOR extends Color,
                              PIECE1 extends Piece<COLOR> & Blockable,
                              PIECE2 extends Piece<Color>>
        extends AbstractTargetActivity<Impact.Type,PIECE1,PIECE2>
        implements Impact<PIECE1> {

    public PieceBlockImpact(PIECE1 source, PIECE2 blocker) {
        super(Impact.Type.BLOCK, source, blocker);
    }

    @Override
    public final String toString() {
        return String.format("%s||%s", getSource(), getTarget());
    }

    @Override
    public final Position getPosition() {
        return getTarget().getPosition();
    }
}