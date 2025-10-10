package com.agutsul.chess.activity.impact;

import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface PieceDiscoveredAttackImpact<COLOR1 extends Color,
                                             COLOR2 extends Color,
                                             PIECE extends Piece<COLOR1>,
                                             ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                             ATTACKED extends Piece<COLOR2>>
        extends Impact<PIECE> {

    enum Mode {
        ABSOLUTE, // discovered check
        RELATIVE
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();

    // utilities

    static boolean isAbsolute(PieceDiscoveredAttackImpact<?,?,?,?,?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PieceDiscoveredAttackImpact.Mode mode) {
        return PieceDiscoveredAttackImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PieceDiscoveredAttackImpact<?,?,?,?,?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PieceDiscoveredAttackImpact.Mode mode) {
        return PieceDiscoveredAttackImpact.Mode.RELATIVE.equals(mode);
    }
}