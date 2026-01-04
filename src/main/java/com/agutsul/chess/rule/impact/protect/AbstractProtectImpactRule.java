package com.agutsul.chess.rule.impact.protect;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.ProtectImpactRule;

abstract class AbstractProtectImpactRule<COLOR extends Color,
                                         PIECE1 extends Piece<COLOR> & Capturable,
                                         PIECE2 extends Piece<COLOR>,
                                         IMPACT extends PieceProtectImpact<COLOR,PIECE1,PIECE2>>
        extends AbstractImpactRule<COLOR,PIECE1,IMPACT>
        implements ProtectImpactRule<COLOR,PIECE1,PIECE2,IMPACT> {

    AbstractProtectImpactRule(Board board) {
        super(board, Impact.Type.PROTECT);
    }
}