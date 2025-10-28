package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public interface PieceInterferenceImpact<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         INTERFERENCOR extends Piece<COLOR1> & Movable,
                                         PROTECTOR extends Piece<COLOR2> & Capturable,
                                         PROTECTED extends Piece<COLOR2>>
        extends Impact<INTERFERENCOR> {

    Line getLine();

    INTERFERENCOR getInterferencor();

    PROTECTED getProtected();

    PROTECTOR getProtector();
}