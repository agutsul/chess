package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public final class PiecePartialPinImpact<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         PINNED extends Piece<COLOR1> & Pinnable,
                                         DEFENDED extends Piece<COLOR1>,
                                         ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractPiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER,
                                       PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER>> {

    public PiecePartialPinImpact(PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER> impact) {
        super(Mode.PARTIAL, impact.getPinned(), impact);
    }

    @Override
    public boolean isMode(Mode mode) {
        return super.isMode(mode)
                || getTarget().isMode(mode);
    }

    @Override
    public ATTACKER getAttacker() {
        return getTarget().getAttacker();
    }

    @Override
    public DEFENDED getDefended() {
        return getTarget().getDefended();
    }

    @Override
    public Line getLine() {
        return getTarget().getLine();
    }
}