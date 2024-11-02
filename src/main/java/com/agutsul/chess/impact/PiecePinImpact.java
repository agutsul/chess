package com.agutsul.chess.impact;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PiecePinImpact<COLOR1 extends Color,
                            COLOR2 extends Color,
                            PIECE extends Piece<COLOR1>,
                            KING extends KingPiece<COLOR1>,
                            ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractTargetImpact<PIECE, AbstractTargetImpact<ATTACKER, KING>>{

    public PiecePinImpact(PIECE piece, KING king, ATTACKER attacker) {
        super(Type.PIN, piece, new PieceCheckImpact<>(attacker, king));
    }

    @Override
    public String getCode() {
        return String.format("%s{%s}", getSource(), getTarget());
    }

    @Override
    public Position getPosition() {
        return getSource().getPosition();
    }
}