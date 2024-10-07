package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.action.AbstractCaptureLineActionRule;

class BishopCaptureActionRule<C1 extends Color,
                              C2 extends Color,
                              BISHOP extends BishopPiece<C1>,
                              PIECE extends Piece<C2> & Capturable>
        extends AbstractCaptureLineActionRule<C1, C2, BISHOP, PIECE,
                                              PieceCaptureAction<C1, C2, BISHOP, PIECE>> {

    BishopCaptureActionRule(Board board, CapturePieceAlgo<C1, BISHOP, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<C1, C2, BISHOP, PIECE>
            createAction(BISHOP bishop, PIECE piece, Line line) {

        return new PieceCaptureAction<C1, C2, BISHOP, PIECE>(bishop, piece, line);
    }
}