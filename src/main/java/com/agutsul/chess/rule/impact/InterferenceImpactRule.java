package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface InterferenceImpactRule<COLOR1 extends Color,
                                        COLOR2 extends Color,
                                        PIECE extends Piece<COLOR1> & Movable,
                                        PROTECTOR extends Piece<COLOR2> & Capturable,
                                        PROTECTED extends Piece<COLOR2>,
                                        IMPACT extends PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED>>
        extends Rule<PIECE,Collection<IMPACT>> {

}