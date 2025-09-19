package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceSkewerImpact<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         ATTACKER extends Piece<COLOR1> & Capturable,
                                         SKEWERED extends Piece<COLOR2>,
                                         DEFENDED extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,
                                       AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,SKEWERED>,
                                       DEFENDED>
        implements PieceSkewerImpact<COLOR1,COLOR2,ATTACKER,SKEWERED,DEFENDED> {

    private final Mode mode;

    AbstractPieceSkewerImpact(Mode mode,
                              AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,SKEWERED> source,
                              DEFENDED target) {

        super(Impact.Type.SKEWER, source, target);
        this.mode = mode;
    }

    @Override
    public final Mode getMode() {
        return mode;
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public final Line getLine() {
        return getSource().getLine().get();
    }

    @Override
    public final ATTACKER getAttacker() {
        return getSource().getSource();
    }

    @Override
    public final SKEWERED getSkewered() {
        return getSource().getTarget();
    }

    @Override
    public final DEFENDED getDefended() {
        return getTarget();
    }
}