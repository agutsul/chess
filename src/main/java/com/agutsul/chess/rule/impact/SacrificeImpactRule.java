package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceSacrificeImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface SacrificeImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     SACRIFICED extends Piece<COLOR1> & Capturable & Movable,
                                     ATTACKER   extends Piece<COLOR2> & Capturable,
                                     IMPACT extends PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>>
        extends Rule<SACRIFICED,Collection<IMPACT>> {

}