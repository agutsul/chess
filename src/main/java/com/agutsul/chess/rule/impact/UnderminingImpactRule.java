package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface UnderminingImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       ATTACKER extends Piece<COLOR1> & Capturable,
                                       ATTACKED extends Piece<COLOR2>,
                                       IMPACT extends PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends Rule<ATTACKER,Collection<IMPACT>> {

}