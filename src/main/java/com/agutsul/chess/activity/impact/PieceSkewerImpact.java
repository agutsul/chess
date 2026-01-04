package com.agutsul.chess.activity.impact;

import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public interface PieceSkewerImpact<COLOR1 extends Color,
                                   COLOR2 extends Color,
                                   ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                   ATTACKED extends Piece<COLOR2>,
                                   DEFENDED extends Piece<COLOR2>>
        extends Impact<ATTACKER> {

    enum Mode {
        ABSOLUTE,
        RELATIVE
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();

    Line getLine();

    ATTACKER getAttacker();

    ATTACKED getAttacked();

    DEFENDED getDefended();

    // utilities

    static boolean isAbsolute(PieceSkewerImpact<?,?,?,?,?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PieceSkewerImpact.Mode mode) {
        return PieceSkewerImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PieceSkewerImpact<?,?,?,?,?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PieceSkewerImpact.Mode mode) {
        return PieceSkewerImpact.Mode.RELATIVE.equals(mode);
    }
}