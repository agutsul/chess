package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceXRayImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface XRayImpactRule<COLOR1 extends Color,
                                COLOR2 extends Color,
                                SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                TARGET extends Piece<?>,
                                IMPACT extends PieceXRayImpact<COLOR1,COLOR2,SOURCE,TARGET>>
        extends Rule<SOURCE,Collection<IMPACT>> {

}