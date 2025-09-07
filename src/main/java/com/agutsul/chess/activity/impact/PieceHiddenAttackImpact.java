package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public class PieceHiddenAttackImpact<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     ATTACKER extends Piece<COLOR1> & Capturable,
                                     PIECE extends Piece<COLOR2>>
        extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,PIECE> {

    public PieceHiddenAttackImpact(ATTACKER attacker, PIECE piece, Line line) {
        super(Impact.Type.ATTACK, attacker, piece, line);
    }

    @Override
    public String toString() {
        return String.format("%sx%s", getSource(), getTarget());
    }
}