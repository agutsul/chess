package com.agutsul.chess.activity.impact;

import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public interface PieceXRayImpact<COLOR1 extends Color,
                                 COLOR2 extends Color,
                                 SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                 TARGET extends Piece<?>>
        extends Impact<AbstractTargetActivity<Impact.Type,SOURCE,TARGET>> {

    enum Mode {
        ABSOLUTE,
        RELATIVE
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();
    Line getLine();

    SOURCE getPiece();
    TARGET getTarget();

    Collection<Piece<?>> getPieces();

    // utilities

    static boolean isAbsolute(PieceXRayImpact<?,?,?,?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PieceXRayImpact.Mode mode) {
        return PieceXRayImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PieceXRayImpact<?,?,?,?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PieceXRayImpact.Mode mode) {
        return PieceXRayImpact.Mode.RELATIVE.equals(mode);
    }
}