package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Backwardable;
import com.agutsul.chess.activity.impact.PieceBackwardImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface BackwardImpactRule<COLOR extends Color,
                                    PIECE extends Piece<COLOR> & Backwardable,
                                    IMPACT extends PieceBackwardImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}