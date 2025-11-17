package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceSacrificeImpact<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            SACRIFICED extends Piece<COLOR1> & Capturable & Movable,
                                            ATTACKER   extends Piece<COLOR2> & Capturable,
                                            SOURCE extends AbstractTargetActivity<Impact.Type,SACRIFICED,?> & Impact<SACRIFICED>,
                                            TARGET extends PieceAttackImpact<COLOR2,COLOR1,ATTACKER,SACRIFICED>>
        extends AbstractTargetActivity<Impact.Type,
                                       AbstractTargetActivity<Impact.Type,SACRIFICED,?>,
                                       TARGET>
        implements PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER> {

    AbstractPieceSacrificeImpact(SOURCE sourceImpact, TARGET sacrificeImpact) {
        super(Impact.Type.SACRIFICE, sourceImpact, sacrificeImpact);
    }

    @Override
    public final SACRIFICED getSacrificed() {
        return getSource().getSource();
    }

    @Override
    public final ATTACKER getAttacker() {
        return getTarget().getSource();
    }

    @Override
    public final Position getPosition() {
        return ((Impact<?>) getSource()).getPosition();
    }
}