package com.agutsul.chess.activity.impact;

import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public interface PieceBlankFileImpact<COLOR extends Color,
                                      PIECE extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable>
        extends Impact<PIECE> {

    enum Mode {
        ABSOLUTE, // open file      => no pawns, fully empty vertical line
        RELATIVE  // semi-open file => one pawn available in vertical line
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();
    Line getLine();

    // utilities

    static boolean isAbsolute(PieceBlankFileImpact<?,?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PieceBlankFileImpact.Mode mode) {
        return PieceBlankFileImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PieceBlankFileImpact<?,?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PieceBlankFileImpact.Mode mode) {
        return PieceBlankFileImpact.Mode.RELATIVE.equals(mode);
    }
}