package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceMonitorImpact<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Capturable>
        extends AbstractTargetActivity<Impact.Type,PIECE, Position>
        implements Impact<PIECE> {

    public PieceMonitorImpact(PIECE piece, Position position) {
        super(Impact.Type.MONITOR, piece, position);
    }

    @Override
    public final String toString() {
        return String.format("%s[%s]", getSource(), getPosition());
    }

    @Override
    public final Position getPosition() {
        return getTarget();
    }
}