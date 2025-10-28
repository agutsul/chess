package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public class PieceCheckImpact<COLOR1 extends Color,
                              COLOR2 extends Color,
                              ATTACKER extends Piece<COLOR1> & Capturable,
                              ATTACKED extends Piece<COLOR2> & Checkable>
        extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    public PieceCheckImpact(ATTACKER attacker, ATTACKED king) {
        super(Impact.Type.CHECK, attacker, king);
    }

    public PieceCheckImpact(ATTACKER attacker, ATTACKED king, boolean hidden) {
        super(Impact.Type.CHECK, attacker, king, hidden);
    }

    public PieceCheckImpact(ATTACKER attacker, ATTACKED king, Line line) {
        super(Impact.Type.CHECK, attacker, king, line);
    }

    public PieceCheckImpact(ATTACKER attacker, ATTACKED king, Line line, boolean hidden) {
        super(Impact.Type.CHECK, attacker, king, line, hidden);
    }

    @Override
    public final String toString() {
        return String.format("%sx%s!", getSource(), getTarget());
    }
}