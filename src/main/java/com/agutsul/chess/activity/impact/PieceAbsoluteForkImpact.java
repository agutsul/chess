package com.agutsul.chess.activity.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PieceAbsoluteForkImpact<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           ATTACKER extends Piece<COLOR1> & Capturable,
                                           FORKED extends Piece<COLOR2>,
                                           IMPACT extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,FORKED>>
        extends AbstractPieceForkImpact<COLOR1,COLOR2,ATTACKER,FORKED,IMPACT> {

    public PieceAbsoluteForkImpact(ATTACKER piece, Collection<IMPACT> impacts) {
        super(Mode.ABSOLUTE, piece, impacts);
    }

}