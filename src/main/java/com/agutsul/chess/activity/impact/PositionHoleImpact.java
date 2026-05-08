package com.agutsul.chess.activity.impact;

import java.util.Objects;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

public interface PositionHoleImpact<POSITION extends Position>
        extends Impact<POSITION> {

    enum Mode {
        ABSOLUTE,   // related to king
        RELATIVE
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();
    Color getColor();

    // utilities

    static boolean isAbsolute(PositionHoleImpact<?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PositionHoleImpact.Mode mode) {
        return PositionHoleImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PositionHoleImpact<?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PositionHoleImpact.Mode mode) {
        return PositionHoleImpact.Mode.RELATIVE.equals(mode);
    }
}