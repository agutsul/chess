package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PieceDeflectionAttackImpact<COLOR1 extends Color,
                                               COLOR2 extends Color,
                                               ATTACKER extends Piece<COLOR1> & Capturable,
                                               ATTACKED extends Piece<COLOR2>,
                                               DEFENDED extends Piece<COLOR2>>
        extends AbstractPieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED> {

    private final AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> attackImpact;

    public PieceDeflectionAttackImpact(AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> attackImpact,
                                       DEFENDED defended) {

        super(attackImpact.getSource(), attackImpact.getTarget(), defended);
        this.attackImpact = attackImpact;
    }

    public AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> getAttackImpact() {
        return attackImpact;
    }
}