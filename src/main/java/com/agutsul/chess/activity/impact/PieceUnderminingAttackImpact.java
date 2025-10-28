package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class PieceUnderminingAttackImpact<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                ATTACKER extends Piece<COLOR1> & Capturable,
                                                ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,
                                               PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    public PieceUnderminingAttackImpact(ATTACKER attacker, ATTACKED attacked) {
        super(new PieceAttackImpact<>(attacker, attacked));
    }

    public PieceUnderminingAttackImpact(ATTACKER attacker, ATTACKED attacked, Line line) {
        super(new PieceAttackImpact<>(attacker, attacked, line));
    }
}