package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractCaptureActionRule<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         PIECE1 extends Piece<COLOR1> & Capturable,
                                         PIECE2 extends Piece<COLOR2>,
                                         ACTION extends PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>
        extends AbstractRule<PIECE1,ACTION, Action.Type>
        implements CaptureActionRule<COLOR1,COLOR2,PIECE1,PIECE2,ACTION> {

    protected AbstractCaptureActionRule(Board board) {
        super(board, Action.Type.CAPTURE);
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
                                                        Collection<Calculatable> next);

    protected abstract Collection<Calculatable> calculate(PIECE1 piece);
}