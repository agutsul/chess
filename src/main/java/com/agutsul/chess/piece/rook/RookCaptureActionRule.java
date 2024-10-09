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

class RookCaptureActionRule<COLOR1 extends Color,
                            COLOR2 extends Color,
                            ROOK extends RookPiece<COLOR1>,
                            PIECE extends Piece<COLOR2> & Capturable>
        extends AbstractCaptureLineActionRule<COLOR1, COLOR2, ROOK, PIECE,
                                              PieceCaptureAction<COLOR1, COLOR2, ROOK, PIECE>> {

    RookCaptureActionRule(Board board, CapturePieceAlgo<COLOR1, ROOK, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<COLOR1, COLOR2, ROOK, PIECE> createAction(ROOK piece1,
                                                                           PIECE piece2,
                                                                           Line line) {

        return new PieceCaptureAction<COLOR1, COLOR2, ROOK, PIECE>(piece1, piece2, line);
    }
}