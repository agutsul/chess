package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public class PieceAttackImpact<COLOR1 extends Color,
                               COLOR2 extends Color,
                               ATTACKER extends Piece<COLOR1> & Capturable,
                               ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    public PieceAttackImpact(ATTACKER attacker, ATTACKED piece) {
        this(attacker, piece, false);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED piece, boolean hidden) {
        super(Impact.Type.ATTACK, attacker, piece, hidden);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED piece, Calculatable calculated) {
        this(attacker, piece, calculated, false);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED piece, Calculatable calculated, boolean hidden) {
        super(Impact.Type.ATTACK, attacker, piece, calculated, hidden);
    }

    @Override
    public String toString() {
        return String.format("%sx%s", getSource(), getTarget());
    }
}