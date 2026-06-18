package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceBlankFileImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface BlankFileImpactRule<COLOR  extends Color,
                                     PIECE  extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable,
                                     IMPACT extends PieceBlankFileImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

}