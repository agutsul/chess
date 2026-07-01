package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PieceAttackImpact<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     ATTACKER extends Piece<COLOR1> & Capturable,
                                     ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    public PieceAttackImpact(ATTACKER attacker, ATTACKED attacked) {
        this(attacker, attacked, false);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED attacked, boolean hidden) {
        this(attacker, attacked, null, hidden);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED attacked, Calculatable calculated) {
        this(attacker, attacked, calculated, false);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED attacked,
                             Calculatable calculated, boolean hidden) {

        super(Impact.Type.ATTACK, attacker, attacked, calculated, hidden);
    }

    @Override
    Integer calculateValue() {
        var value = super.calculateValue();
        if (((Protectable) getTarget()).isProtected()) {
            // if target piece is protected attacking it means loosing source piece ( attacker )
            return value + Math.negateExact(getSource().getValue());
        }

        var diff = Math.abs(getSource().getValue()) - Math.abs(getTarget().getValue());
        return value + getSource().getDirection() * Math.abs(diff);
    }
}