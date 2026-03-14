package com.agutsul.chess.rule.impact.motion;

import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceMotionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.MotionImpactRule;

abstract class AbstractMotionImpactRule<COLOR extends Color,
                                        PIECE extends Piece<COLOR> & Movable,
                                        IMPACT extends PieceMotionImpact<COLOR,PIECE>>
        extends AbstractImpactRule<COLOR,PIECE,IMPACT>
        implements MotionImpactRule<COLOR,PIECE,IMPACT> {

    AbstractMotionImpactRule(Board board) {
        super(board, Impact.Type.MOTION);
    }
}