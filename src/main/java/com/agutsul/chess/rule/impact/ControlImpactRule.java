package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceControlImpact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface ControlImpactRule<COLOR extends Color,
                                   PIECE extends Piece<COLOR> & Capturable,
                                   IMPACT extends PieceControlImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}