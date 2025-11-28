package com.agutsul.chess.activity.impact;

import org.apache.commons.lang3.StringUtils;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceDesperadoAttackImpact<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              DESPERADO extends Piece<COLOR1> & Capturable,
                                              ATTACKER  extends Piece<COLOR2> & Capturable,
                                              ATTACKED  extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,
                                       AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED>,
                                       AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DESPERADO>>
        implements PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                        AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED>> {

    private final Mode mode;

    public PieceDesperadoAttackImpact(Mode mode,
                                      AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED> source,
                                      AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DESPERADO> target) {

        super(Impact.Type.DESPERADO, source, target);
        this.mode = mode;
    }

    @Override
    public ATTACKER getAttacker() {
        return getTarget() != null
                ? getTarget().getSource()
                : null;
    }

    @Override
    public ATTACKED getAttacked() {
        return getSource().getTarget();
    }

    @Override
    public DESPERADO getDesperado() {
        return getSource().getSource();
    }

    @Override
    public Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public String toString() {
        return String.format("[ %s ] => [ %s ]", getSource(),
                getTarget() == null ? StringUtils.EMPTY : getTarget()
        );
    }

    @Override
    public Mode getMode() {
        return this.mode;
    }
}