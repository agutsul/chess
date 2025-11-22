package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceLuftImpact<COLOR extends Color,
                             PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractTargetActivity<Impact.Type,PIECE,Position>
        implements Impact<PIECE> {

    public PieceLuftImpact(PIECE piece, Position position) {
        super(Impact.Type.LUFT, piece, position);
    }

    @Override
    public final String toString() {
        return String.format("%s-->%s", getSource(), getTarget());
    }

    @Override
    public final Position getPosition() {
        // luft is position left by the pawn while performing an action
        return getSource().getPosition();
    }
}