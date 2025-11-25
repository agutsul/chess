package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PieceDesperadoAttackImpact<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              DESPERADO extends Piece<COLOR1> & Capturable,
                                              ATTACKER  extends Piece<COLOR2> & Capturable,
                                              ATTACKED  extends Piece<COLOR2>>
        extends AbstractPieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                             AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED>,
                                             AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DESPERADO>> {

    public PieceDesperadoAttackImpact(AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED> source,
                                      AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DESPERADO> target) {

        super(source, target);
    }

    @Override
    public String toString() {
        return String.format("[ %s ] => [ %s ]", getSource(), getTarget());
    }
}