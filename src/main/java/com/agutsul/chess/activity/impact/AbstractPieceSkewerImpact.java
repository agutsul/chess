package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceSkewerImpact<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         ATTACKER extends Piece<COLOR1> & Capturable,
                                         ATTACKED extends Piece<COLOR2>,
                                         DEFENDED extends Piece<COLOR2>,
                                         IMPACT extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,DEFENDED>
        implements PieceSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED> {

    private final Mode mode;
    private final IMPACT impact;

    AbstractPieceSkewerImpact(Mode mode, IMPACT impact, DEFENDED target) {
        super(Impact.Type.SKEWER, impact.getSource(), target);
        this.mode = mode;
        this.impact = impact;
    }

    @Override
    public final Mode getMode() {
        return mode;
    }

    @Override
    public final Position getPosition() {
        return impact.getPosition();
    }

    @Override
    public final Line getLine() {
        return impact.getLine().get();
    }

    @Override
    public final ATTACKER getAttacker() {
        return impact.getSource();
    }

    @Override
    public final ATTACKED getAttacked() {
        return impact.getTarget();
    }

    @Override
    public final DEFENDED getDefended() {
        return getTarget();
    }
}