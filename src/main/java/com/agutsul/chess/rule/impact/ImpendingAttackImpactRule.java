package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceImpendingAttackImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface ImpendingAttackImpactRule<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                           ATTACKED extends Piece<COLOR2>,
                                           IMPACT extends PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends Rule<ATTACKER,Collection<IMPACT>> {

}