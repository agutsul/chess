package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Blockable;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface BlockImpactRule<COLOR extends Color,
                                 PIECE1 extends Piece<COLOR> & Blockable,
                                 PIECE2 extends Piece<Color>,
                                 IMPACT extends PieceBlockImpact<COLOR,PIECE1,PIECE2>>
        extends Rule<PIECE1,Collection<IMPACT>> {

}