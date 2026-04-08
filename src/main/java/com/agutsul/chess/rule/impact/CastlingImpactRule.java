package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceCastlingImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface CastlingImpactRule<COLOR  extends Color,
                                    PIECE1 extends Piece<COLOR> & Movable & Castlingable,
                                    PIECE2 extends Piece<COLOR> & Movable & Castlingable,
                                    IMPACT extends PieceCastlingImpact<COLOR,PIECE1,PIECE2>>
        extends Rule<PIECE1,Collection<IMPACT>> {

}