package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PiecePartialPinImpact<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         PINNED extends Piece<COLOR1> & Movable & Capturable & Pinnable,
                                         DEFENDED extends Piece<COLOR1>,
                                         ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends AbstractSourceActivity<Impact.Type,PINNED>
        implements PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER> {

    private PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER> impact;

    public PiecePartialPinImpact(PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER> impact) {
        super(Impact.Type.PIN, impact.getSource());
        this.impact = impact;
    }

    @Override
    public Mode getMode() {
        return Mode.PARTIAL;
    }

    @Override
    public final Integer getValue() {
        return impact.getValue();
    }

    @Override
    public boolean isMode(Mode mode) {
        return PiecePinImpact.super.isMode(mode)
                || impact.isMode(mode);
    }

    @Override
    public PINNED getPinned() {
        return impact.getPinned();
    }

    @Override
    public ATTACKER getAttacker() {
        return impact.getAttacker();
    }

    @Override
    public DEFENDED getDefended() {
        return impact.getDefended();
    }

    @Override
    public Line getLine() {
        return impact.getLine();
    }

    @Override
    public Position getPosition() {
        return impact.getPosition();
    }

    @Override
    public String toString() {
        return String.valueOf(impact);
    }
}