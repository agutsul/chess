package com.agutsul.chess.activity.impact;

import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

/*
    A desperado occurs when a piece is threatened or trapped
    but captures an opponent's piece before being captured itself,
    to gain as much material as possible,
    or alternatively when both sides have hanging pieces
    and we sacrifice a piece in order to gain more material at the end.
*/
public interface PieceDesperadoImpact<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      DESPERADO extends Piece<COLOR1> & Capturable,
                                      ATTACKER  extends Piece<COLOR2> & Capturable,
                                      ATTACKED  extends Piece<COLOR2>,
                                      SOURCE>
        extends Impact<SOURCE> {

    enum Mode {
        ABSOLUTE,   // trapped piece
        RELATIVE    // gain more material
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();

    ATTACKER getAttacker();

    ATTACKED getAttacked();

    DESPERADO getDesperado();

    // utilities

    static boolean isAbsolute(PieceDesperadoImpact<?,?,?,?,?,?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PieceDesperadoImpact.Mode mode) {
        return PieceDesperadoImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PieceDesperadoImpact<?,?,?,?,?,?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PieceDesperadoImpact.Mode mode) {
        return PieceDesperadoImpact.Mode.RELATIVE.equals(mode);
    }
}