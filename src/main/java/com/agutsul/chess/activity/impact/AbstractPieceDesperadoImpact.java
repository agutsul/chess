package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceDesperadoImpact<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            DESPERADO extends Piece<COLOR1> & Capturable,
                                            ATTACKER extends Piece<COLOR2> & Capturable,
                                            ATTACKED extends Piece<COLOR2>,
                                            SOURCE extends AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED>,
                                            TARGET extends AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DESPERADO>>
        extends AbstractTargetActivity<Impact.Type,
                                       AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED>,
                                       TARGET>
        implements PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> {

    AbstractPieceDesperadoImpact(SOURCE source, TARGET target) {
        super(Impact.Type.DESCPERADO, source, target);
    }

    @Override
    public final ATTACKER getAttacker() {
        return getTarget().getSource();
    }

    @Override
    public final ATTACKED getAttacked() {
        return getSource().getTarget();
    }

    @Override
    public final DESPERADO getDesperado() {
        return getSource().getSource();
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }
}