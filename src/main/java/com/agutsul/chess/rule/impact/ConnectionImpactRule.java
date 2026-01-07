package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Connectable;
import com.agutsul.chess.activity.impact.PieceConnectionImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface ConnectionImpactRule<COLOR extends Color,
                                      PIECE extends Piece<COLOR> & Connectable,
                                      IMPACT extends PieceConnectionImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}