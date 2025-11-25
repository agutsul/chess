package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface DesperadoImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     DESPERADO extends Piece<COLOR1> & Capturable,
                                     ATTACKER extends Piece<COLOR2> & Capturable,
                                     ATTACKED extends Piece<COLOR2>,
                                     IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>>
        extends Rule<DESPERADO,Collection<IMPACT>> {

}