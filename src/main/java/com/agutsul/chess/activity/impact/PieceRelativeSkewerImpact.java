package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public final class PieceRelativeSkewerImpact<COLOR1 extends Color,
                                             COLOR2 extends Color,
                                             ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                             ATTACKED extends Piece<COLOR2>,
                                             DEFENDED extends Piece<COLOR2>>
        extends AbstractPieceSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,
                                          PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    public PieceRelativeSkewerImpact(ATTACKER attacker, ATTACKED skewered, DEFENDED defended, Line line) {
        super(Mode.RELATIVE,
                new PieceAttackImpact<>(attacker, skewered, line),
                defended
        );
    }
}