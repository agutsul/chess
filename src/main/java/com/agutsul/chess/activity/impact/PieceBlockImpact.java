package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public interface PieceBlockImpact<COLOR1 extends Color,
                                  COLOR2 extends Color,
                                  BLOCKER  extends Piece<COLOR1> & Movable,
                                  ATTACKED extends Piece<COLOR1>,
                                  ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends Impact<BLOCKER> {

    Line getLine();

    BLOCKER getBlocker();

    ATTACKED getAttacked();

    ATTACKER getAttacker();
}