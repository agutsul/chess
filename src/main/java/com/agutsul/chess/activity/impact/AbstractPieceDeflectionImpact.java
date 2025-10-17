package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceDeflectionImpact<COLOR1 extends Color,
                                             COLOR2 extends Color,
                                             ATTACKER extends Piece<COLOR1> & Capturable,
                                             ATTACKED extends Piece<COLOR2>,
                                             DEFENDED extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,ATTACKED>
        implements PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED> {

    private final DEFENDED defended;

    AbstractPieceDeflectionImpact(ATTACKER attacker, ATTACKED attacked, DEFENDED defended) {
        super(Impact.Type.DEFLECTION, attacker, attacked);
        this.defended = defended;
    }

    @Override
    public final ATTACKER getAttacker() {
        return getSource();
    }

    @Override
    public final ATTACKED getAttacked() {
        return getTarget();
    }

    @Override
    public final DEFENDED getDefended() {
        return this.defended;
    }

    @Override
    public final Position getPosition() {
        return getTarget().getPosition();
    }
}