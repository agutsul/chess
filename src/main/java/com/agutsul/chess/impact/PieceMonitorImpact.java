package com.agutsul.chess.impact;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceMonitorImpact<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Capturable>
        extends AbstractTargetImpact<PIECE, Position> {

    public PieceMonitorImpact(PIECE piece, Position position) {
        super(Type.MONITOR, piece, position);
    }

    @Override
    public String getCode() {
        return String.format("%s[%s]", getSource(), getTarget());
    }

    @Override
    public Position getPosition() {
        return getTarget();
    }
}