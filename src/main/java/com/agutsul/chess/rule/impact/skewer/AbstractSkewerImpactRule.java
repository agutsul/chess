package com.agutsul.chess.rule.impact.skewer;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceSkewerImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.SkewerImpactRule;

abstract class AbstractSkewerImpactRule<COLOR1 extends Color,
                                        COLOR2 extends Color,
                                        ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                        ATTACKED extends Piece<COLOR2>,
                                        DEFENDED extends Piece<COLOR2>,
                                        IMPACT extends PieceSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
        extends AbstractImpactRule<COLOR1,ATTACKER,IMPACT>
        implements SkewerImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,IMPACT> {

    AbstractSkewerImpactRule(Board board) {
        super(board, Impact.Type.SKEWER);
    }
}