package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface DiscoveredAttackImpactRule<COLOR extends Color,
                                            PIECE  extends Piece<COLOR>,
                                            IMPACT extends PieceDiscoveredAttackImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}