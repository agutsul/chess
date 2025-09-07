package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public final class PieceRelativePinImpact<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          PINNED extends Piece<COLOR1> & Pinnable,
                                          DEFENDED extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractPiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER,
                                       PieceHiddenAttackImpact<COLOR2,COLOR1,ATTACKER,DEFENDED>> {

    public PieceRelativePinImpact(PINNED piece, DEFENDED target, ATTACKER attacker, Line line) {
        super(Mode.RELATIVE, piece, new PieceHiddenAttackImpact<>(attacker, target, line));
    }

    @Override
    public ATTACKER getAttacker() {
        return getTarget().getSource();
    }

    @Override
    public DEFENDED getDefended() {
        return getTarget().getTarget();
    }

    @Override
    public Line getLine() {
        return getTarget().getLine().get();
    }
}