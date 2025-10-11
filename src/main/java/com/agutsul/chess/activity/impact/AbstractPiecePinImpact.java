package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPiecePinImpact<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      PINNED extends Piece<COLOR1> & Pinnable,
                                      DEFENDED extends Piece<COLOR1>,
                                      ATTACKER extends Piece<COLOR2> & Capturable,
                                      IMPACT extends AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DEFENDED>>
        extends AbstractTargetActivity<Impact.Type,PINNED,IMPACT>
        implements PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER> {

    private final Mode mode;

    AbstractPiecePinImpact(Mode mode, PINNED piece, IMPACT impact) {
        super(Impact.Type.PIN, piece, impact);
        this.mode = mode;
    }

    @Override
    public final String toString() {
        return String.format("%s{%s}", getPinned(), getTarget());
    }

    @Override
    public final Position getPosition() {
        return getPinned().getPosition();
    }

    @Override
    public final PINNED getPinned() {
        return getSource();
    }

    @Override
    public final Mode getMode() {
        return mode;
    }
}