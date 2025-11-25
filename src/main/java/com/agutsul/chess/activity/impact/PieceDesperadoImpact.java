package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface PieceDesperadoImpact<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      DESPERADO extends Piece<COLOR1> & Capturable,
                                      ATTACKER extends Piece<COLOR2> & Capturable,
                                      ATTACKED extends Piece<COLOR2>>
        extends Impact<AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED>> {

    ATTACKER getAttacker();

    ATTACKED getAttacked();

    DESPERADO getDesperado();
}