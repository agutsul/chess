package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Accumulatable;
import com.agutsul.chess.activity.impact.PieceAccumulationImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface AccumulationImpactRule<COLOR extends Color,
                                        PIECE extends Piece<COLOR> & Accumulatable,
                                        IMPACT extends PieceAccumulationImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}