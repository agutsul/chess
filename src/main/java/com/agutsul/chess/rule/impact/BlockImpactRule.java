package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface BlockImpactRule<COLOR1 extends Color,
                                 COLOR2 extends Color,
                                 BLOCKER extends Piece<COLOR1> & Movable,
                                 ATTACKED extends Piece<COLOR1>,
                                 ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                 IMPACT extends PieceBlockImpact<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER>>
        extends Rule<BLOCKER,Collection<IMPACT>> {

}