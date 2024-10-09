package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractCaptureActionRule<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         PIECE1 extends Piece<COLOR1> & Capturable,
                                         PIECE2 extends Piece<COLOR2> & Capturable,
                                         ACTION extends PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>
        extends AbstractRule<PIECE1, ACTION>
        implements CaptureActionRule<COLOR1, COLOR2, PIECE1, PIECE2, ACTION> {

    protected AbstractCaptureActionRule(Board board) {
        super(board);
    }

    @Override
    public final Collection<ACTION> evaluate(PIECE1 piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createActions(piece, next);
    }

    protected abstract Collection<ACTION> createActions(PIECE1 piece,
                                                        Collection<Calculated> next);

    protected abstract Collection<Calculated> calculate(PIECE1 piece);
}