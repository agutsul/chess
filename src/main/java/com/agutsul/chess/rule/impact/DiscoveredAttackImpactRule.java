package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface DiscoveredAttackImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            PIECE  extends Piece<COLOR1>,
                                            ATTACKER extends Piece<COLOR1> & Capturable,
                                            ATTACKED extends Piece<COLOR2>,
                                            IMPACT extends PieceDiscoveredAttackImpact<COLOR1,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}