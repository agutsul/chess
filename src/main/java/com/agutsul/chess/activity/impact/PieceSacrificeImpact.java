package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface PieceSacrificeImpact<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      SACRIFICED extends Piece<COLOR1> & Capturable & Movable,
                                      ATTACKER   extends Piece<COLOR2> & Capturable>
        extends Impact<AbstractTargetActivity<Impact.Type,SACRIFICED,?>> {

    SACRIFICED getSacrificed();

    ATTACKER getAttacker();
}