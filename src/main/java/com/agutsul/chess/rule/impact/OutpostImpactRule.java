package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceOutpostImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface OutpostImpactRule<COLOR extends Color,
                                   PIECE extends Piece<COLOR> & Capturable & Movable,
                                   IMPACT extends PieceOutpostImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}