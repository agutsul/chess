package com.agutsul.chess.activity.impact;

import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public interface PieceUnderminingImpact<COLOR1 extends Color,
                                        COLOR2 extends Color,
                                        ATTACKER extends Piece<COLOR1> & Capturable,
                                        ATTACKED extends Piece<COLOR2>>
        extends Impact<ATTACKER> {

    ATTACKER getAttacker();

    ATTACKED getAttacked();

    Optional<Line> getLine();
}