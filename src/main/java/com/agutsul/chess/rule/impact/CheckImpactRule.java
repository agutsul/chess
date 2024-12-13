package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceCheckImpact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface CheckImpactRule<COLOR1 extends Color,
                                 COLOR2 extends Color,
                                 ATTACKER extends Piece<COLOR1> & Capturable,
                                 KING extends Piece<COLOR2> & Checkable,
                                 IMPACT extends PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>>
        extends Rule<ATTACKER,Collection<IMPACT>> {

}