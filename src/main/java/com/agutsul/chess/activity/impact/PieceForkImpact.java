package com.agutsul.chess.activity.impact;

import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface PieceForkImpact<COLOR1 extends Color,
                                 COLOR2 extends Color,
                                 ATTACKER extends Piece<COLOR1> & Capturable,
                                 FORKED extends Piece<COLOR2>>
        extends Impact<ATTACKER> {

    enum Mode {
        ABSOLUTE,
        RELATIVE
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();

    // utilities

    static boolean isAbsolute(PieceForkImpact<?,?,?,?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PieceForkImpact.Mode mode) {
        return PieceForkImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PieceForkImpact<?,?,?,?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PieceForkImpact.Mode mode) {
        return PieceForkImpact.Mode.RELATIVE.equals(mode);
    }
}