package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface MonitorImpactRule<COLOR extends Color,
                                   PIECE extends Piece<COLOR> & Capturable,
                                   IMPACT extends PieceMonitorImpact<COLOR, PIECE>>
        extends Rule<PIECE, Collection<IMPACT>> {

}