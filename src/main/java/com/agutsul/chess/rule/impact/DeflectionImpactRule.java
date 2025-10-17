package com.agutsul.chess.rule.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDeflectionImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface DeflectionImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      ATTACKER extends Piece<COLOR1> & Capturable,
                                      ATTACKED extends Piece<COLOR2>,
                                      DEFENDED extends Piece<COLOR2>,
                                      IMPACT extends PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>> {

}