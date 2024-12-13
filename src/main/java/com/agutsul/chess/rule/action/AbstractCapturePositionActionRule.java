package com.agutsul.chess.rule.action;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public abstract class AbstractCapturePositionActionRule<COLOR1 extends Color,
                                                        COLOR2 extends Color,
                                                        PIECE1 extends Piece<COLOR1> & Capturable,
                                                        PIECE2 extends Piece<COLOR2>,
                                                        ACTION extends PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>
        extends AbstractCaptureActionRule<COLOR1,COLOR2,PIECE1,PIECE2,ACTION> {

    protected final CapturePieceAlgo<COLOR1,PIECE1,Calculated> algo;

    protected AbstractCapturePositionActionRule(Board board,
                                                CapturePieceAlgo<COLOR1,PIECE1,Calculated> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        return algo.calculate(piece);
    }

    @Override
    protected Collection<ACTION> createActions(PIECE1 piece1,
                                               Collection<Calculated> next) {
        var actions = new ArrayList<ACTION>();
        for (var position : next) {
            var optionalPiece = board.getPiece((Position) position);
            if (optionalPiece.isEmpty()) {
                continue;
            }

            @SuppressWarnings("unchecked")
            var piece2 = (PIECE2) optionalPiece.get();
            if (piece2.getColor() == piece1.getColor()) {
                continue;
            }

            actions.add(createAction(piece1, piece2));
        }

        return actions;
    }

    protected abstract ACTION createAction(PIECE1 piece1, PIECE2 piece2);
}