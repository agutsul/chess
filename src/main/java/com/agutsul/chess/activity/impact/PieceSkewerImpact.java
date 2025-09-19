package com.agutsul.chess.activity.impact;

import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public interface PieceSkewerImpact<COLOR1 extends Color,
                                   COLOR2 extends Color,
                                   ATTACKER extends Piece<COLOR1> & Capturable,
                                   SKEWERED extends Piece<COLOR2>,
                                   DEFENDED extends Piece<COLOR2>>
        extends Impact<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,SKEWERED>> {

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

    SKEWERED getSkewered();

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