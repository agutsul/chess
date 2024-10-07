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

abstract class AbstractCaptureActionRule<C1 extends Color,
                                         C2 extends Color,
                                         P1 extends Piece<C1> & Capturable,
                                         P2 extends Piece<C2> & Capturable,
                                         A extends PieceCaptureAction<C1,C2,P1,P2>>
        extends AbstractRule<P1, A>
        implements CaptureActionRule<C1, C2, P1, P2, A> {

    protected AbstractCaptureActionRule(Board board) {
        super(board);
    }

    @Override
    public final Collection<A> evaluate(P1 piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createActions(piece, next);
    }

    protected abstract Collection<A> createActions(P1 piece, Collection<Calculated> next);

    protected abstract Collection<Calculated> calculate(P1 piece);
}