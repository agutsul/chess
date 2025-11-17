package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PieceSacrificeMoveImpact<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            SACRIFICED extends Piece<COLOR1> & Capturable & Movable,
                                            ATTACKER   extends Piece<COLOR2> & Capturable>
        extends AbstractPieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER,
                                             PieceControlImpact<COLOR1,SACRIFICED>,
                                             PieceAttackImpact<COLOR2,COLOR1,ATTACKER,SACRIFICED>> {

    public PieceSacrificeMoveImpact(PieceControlImpact<COLOR1,SACRIFICED> source,
                                    PieceAttackImpact<COLOR2,COLOR1,ATTACKER,SACRIFICED> target) {

        super(source, target);
    }

    @Override
    public String toString() {
        return String.format("%sx(%s %s)",
                getAttacker(), getSacrificed(), getPosition()
        );
    }
}