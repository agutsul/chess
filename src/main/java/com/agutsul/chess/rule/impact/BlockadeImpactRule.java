package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Blockadable;
import com.agutsul.chess.activity.impact.PieceBlockadeImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface BlockadeImpactRule<COLOR extends Color,
                                    PIECE1 extends Piece<COLOR> & Blockadable,
                                    PIECE2 extends Piece<Color>,
                                    IMPACT extends PieceBlockadeImpact<COLOR,PIECE1,PIECE2>>
        extends Rule<PIECE1,Collection<IMPACT>> {

}