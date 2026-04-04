package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Promotable;
import com.agutsul.chess.activity.impact.PiecePromoteImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface PromoteImpactRule<COLOR  extends Color,
                                   PIECE  extends Piece<COLOR> & Promotable,
                                   IMPACT extends PiecePromoteImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}