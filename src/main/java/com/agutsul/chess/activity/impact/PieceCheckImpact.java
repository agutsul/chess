package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public class PieceCheckImpact<COLOR1 extends Color,
                              COLOR2 extends Color,
                              ATTACKER extends Piece<COLOR1> & Capturable,
                              KING extends Piece<COLOR2> & Checkable>
        extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,KING> {

    public PieceCheckImpact(ATTACKER attacker, KING king) {
        super(Impact.Type.CHECK, attacker, king);
    }

    public PieceCheckImpact(ATTACKER attacker, KING king, Line line) {
        super(Impact.Type.CHECK, attacker, king, line);
    }

    @Override
    public final String toString() {
        return String.format("%sx%s!", getSource(), getTarget());
    }
}