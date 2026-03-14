package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceMotionImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface MotionImpactRule<COLOR extends Color,
                                  PIECE extends Piece<COLOR> & Movable,
                                  IMPACT extends PieceMotionImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}