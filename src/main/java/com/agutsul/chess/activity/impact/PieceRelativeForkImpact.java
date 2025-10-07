package com.agutsul.chess.activity.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PieceRelativeForkImpact<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           ATTACKER extends Piece<COLOR1> & Capturable,
                                           ATTACKED extends Piece<COLOR2>,
                                           IMPACT extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractPieceForkImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    public PieceRelativeForkImpact(ATTACKER piece, Collection<IMPACT> impacts) {
        super(Mode.RELATIVE, piece, impacts);
    }
}