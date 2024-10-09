package com.agutsul.chess.impact;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceCheckImpact<COLOR1 extends Color,
                              COLOR2 extends Color,
                              PIECE extends Piece<COLOR1> & Capturable,
                              KING extends KingPiece<COLOR2>>
        extends AbstractTargetImpact<PIECE, KING> {

    public PieceCheckImpact(PIECE attacker, KING king) {
        super(Type.CHECK, attacker, king);
    }

    @Override
    public String getCode() {
        return String.format("%sx%s!", getSource(), getTarget());
    }

    @Override
    public Position getPosition() {
        return getSource().getPosition();
    }
}