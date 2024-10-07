package com.agutsul.chess.piece.rook;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.action.AbstractCaptureLineActionRule;

class RookCaptureActionRule<C1 extends Color,
                            C2 extends Color,
                            ROOK extends RookPiece<C1>,
                            PIECE extends Piece<C2> & Capturable>
        extends AbstractCaptureLineActionRule<C1, C2, ROOK, PIECE,
                                              PieceCaptureAction<C1, C2, ROOK, PIECE>> {

    RookCaptureActionRule(Board board, CapturePieceAlgo<C1, ROOK, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<C1, C2, ROOK, PIECE>
            createAction(ROOK piece1, PIECE piece2, Line line) {

        return new PieceCaptureAction<C1, C2, ROOK, PIECE>(piece1, piece2, line);
    }
}