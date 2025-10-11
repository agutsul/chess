package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public interface PieceBlockImpact<COLOR1 extends Color,
                                  COLOR2 extends Color,
                                  BLOCKER extends Piece<COLOR1>,
                                  DEFENDED extends Piece<COLOR1>,
                                  ATTACKER extends Piece<COLOR2> & Capturable>
        extends Impact<BLOCKER> {

    Line getLine();

    BLOCKER getBlocker();

    DEFENDED getDefended();

    ATTACKER getAttacker();
}