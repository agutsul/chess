package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Isolatable;
import com.agutsul.chess.activity.impact.PieceIsolationImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface IsolationImpactRule<COLOR extends Color,
                                     PIECE extends Piece<COLOR> & Isolatable,
                                     IMPACT extends PieceIsolationImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}