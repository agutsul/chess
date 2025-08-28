package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Stagnantable;
import com.agutsul.chess.activity.impact.PieceStagnantImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface StagnantImpactRule<COLOR extends Color,
                                    PIECE1 extends Piece<COLOR> & Stagnantable,
                                    PIECE2 extends Piece<Color>,
                                    IMPACT extends PieceStagnantImpact<COLOR,PIECE1,PIECE2>>
        extends Rule<PIECE1,Collection<IMPACT>> {

}