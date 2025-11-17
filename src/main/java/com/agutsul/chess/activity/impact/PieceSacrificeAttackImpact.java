package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PieceSacrificeAttackImpact<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              SACRIFICED extends Piece<COLOR1> & Capturable & Movable,
                                              ATTACKER extends Piece<COLOR2> & Capturable,
                                              ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER,
                                             PieceAttackImpact<COLOR1,COLOR2,SACRIFICED,ATTACKED>,
                                             PieceAttackImpact<COLOR2,COLOR1,ATTACKER,SACRIFICED>> {

    // attack opponent piece and sacrifice on that position
    public PieceSacrificeAttackImpact(PieceAttackImpact<COLOR1,COLOR2,SACRIFICED,ATTACKED> source,
                                      PieceAttackImpact<COLOR2,COLOR1,ATTACKER,SACRIFICED> target) {

        super(source, target);
    }

    @Override
    public String toString() {
        return String.format("%sx(%sx%s)",
                getAttacker(), getSacrificed(), getSource().getTarget()
        );
    }
}