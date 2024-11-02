package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface ProtectImpactRule<COLOR extends Color,
                                   PIECE1 extends Piece<COLOR> & Capturable,
                                   PIECE2 extends Piece<COLOR>,
                                   IMPACT extends PieceProtectImpact<COLOR,PIECE1,PIECE2>>
        extends Rule<PIECE1, Collection<IMPACT>> {

}