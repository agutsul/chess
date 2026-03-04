package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class PieceCheckImpact<COLOR1 extends Color,
                                    COLOR2 extends Color,
                                    ATTACKER extends Piece<COLOR1> & Capturable,
                                    ATTACKED extends Piece<COLOR2> & Checkable>
        extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    public PieceCheckImpact(ATTACKER attacker, ATTACKED king) {
        this(attacker, king, false);
    }

    public PieceCheckImpact(ATTACKER attacker, ATTACKED king, boolean hidden) {
        this(attacker, king, null, hidden);
    }

    public PieceCheckImpact(ATTACKER attacker, ATTACKED king, Line line) {
        this(attacker, king, line, false);
    }

    public PieceCheckImpact(ATTACKER attacker, ATTACKED king,
                            Line line, boolean hidden) {

        super(Impact.Type.CHECK, attacker, king, line, hidden);
    }

    @Override
    public String toString() {
        return String.format("%sx%s!", getSource(), getTarget());
    }
}