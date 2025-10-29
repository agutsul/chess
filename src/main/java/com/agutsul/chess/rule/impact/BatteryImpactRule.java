package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceBatteryImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface BatteryImpactRule<COLOR extends Color,
                                   PIECE1 extends Piece<COLOR> & Capturable & Movable & Lineable,
                                   PIECE2 extends Piece<COLOR> & Capturable & Movable & Lineable,
                                   IMPACT extends PieceBatteryImpact<COLOR,PIECE1,PIECE2>>
        extends Rule<PIECE1,Collection<IMPACT>> {

}