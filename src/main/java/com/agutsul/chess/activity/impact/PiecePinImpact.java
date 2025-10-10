package com.agutsul.chess.activity.impact;

import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public interface PiecePinImpact<COLOR1 extends Color,
                                COLOR2 extends Color,
                                PINNED extends Piece<COLOR1> & Pinnable,
                                DEFENDED extends Piece<COLOR1>,
                                ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends Impact<PINNED> {

    enum Mode {
        ABSOLUTE,
        RELATIVE,
        PARTIAL
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();

    Line getLine();

    ATTACKER getAttacker();

    PINNED getPinned();

    DEFENDED getDefended();

    // utilities

    static boolean isAbsolute(PiecePinImpact<?,?,?,?,?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PiecePinImpact.Mode mode) {
        return PiecePinImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PiecePinImpact<?,?,?,?,?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PiecePinImpact.Mode mode) {
        return PiecePinImpact.Mode.RELATIVE.equals(mode);
    }

    static boolean isPartial(PiecePinImpact<?,?,?,?,?> impact) {
        return isPartial(impact.getMode());
    }

    static boolean isPartial(PiecePinImpact.Mode mode) {
        return PiecePinImpact.Mode.PARTIAL.equals(mode);
    }
}