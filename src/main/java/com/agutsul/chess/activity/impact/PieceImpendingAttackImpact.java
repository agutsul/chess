package com.agutsul.chess.activity.impact;

import java.util.Objects;
import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public interface PieceImpendingAttackImpact<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                            ATTACKED extends Piece<COLOR2>>
        extends Impact<AbstractTargetActivity<Impact.Type,ATTACKER,?>> {

    enum Mode {
        ABSOLUTE, // check
        RELATIVE
    }

    default boolean isMode(Mode mode) {
        return Objects.equals(getMode(), mode);
    }

    Mode getMode();

    ATTACKER getAttacker();

    ATTACKED getAttacked();

    Optional<Line> getLine();

    // utilities

    static boolean isAbsolute(PieceImpendingAttackImpact<?,?,?,?> impact) {
        return isAbsolute(impact.getMode());
    }

    static boolean isAbsolute(PieceImpendingAttackImpact.Mode mode) {
        return PieceImpendingAttackImpact.Mode.ABSOLUTE.equals(mode);
    }

    static boolean isRelative(PieceImpendingAttackImpact<?,?,?,?> impact) {
        return isRelative(impact.getMode());
    }

    static boolean isRelative(PieceImpendingAttackImpact.Mode mode) {
        return PieceImpendingAttackImpact.Mode.RELATIVE.equals(mode);
    }
}