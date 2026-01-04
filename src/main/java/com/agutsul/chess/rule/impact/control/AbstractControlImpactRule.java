package com.agutsul.chess.rule.impact.control;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.ControlImpactRule;

abstract class AbstractControlImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable & Movable,
                                         IMPACT extends PieceControlImpact<COLOR,PIECE>>
        extends AbstractImpactRule<COLOR,PIECE,IMPACT>
        implements ControlImpactRule<COLOR,PIECE,IMPACT> {

    AbstractControlImpactRule(Board board) {
        super(board, Impact.Type.CONTROL);
    }
}