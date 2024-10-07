package com.agutsul.chess.rule.action;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public abstract class AbstractCapturePositionActionRule<C1 extends Color,
                                                        C2 extends Color,
                                                        P1 extends Piece<C1> & Capturable,
                                                        P2 extends Piece<C2> & Capturable,
                                                        A extends PieceCaptureAction<C1,C2,P1,P2>>
        extends AbstractCaptureActionRule<C1, C2, P1, P2, A> {

    private final CapturePieceAlgo<C1, P1, Calculated> algo;

    protected AbstractCapturePositionActionRule(Board board,
                                                CapturePieceAlgo<C1, P1, Calculated> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(P1 piece) {
        return algo.calculate(piece);
    }

    @Override
    protected Collection<A> createActions(P1 piece1, Collection<Calculated> next) {
        var actions = new ArrayList<A>();
        for (var position : next) {
            var optionalPiece = board.getPiece((Position) position);
            if (optionalPiece.isEmpty()) {
                continue;
            }

            @SuppressWarnings("unchecked")
            var piece2 = (P2) optionalPiece.get();
            if (piece2.getColor() == piece1.getColor()) {
                continue;
            }

            actions.add(createAction(piece1, piece2));
        }

        return actions;
    }

    protected abstract A createAction(P1 piece1, P2 piece2);
}