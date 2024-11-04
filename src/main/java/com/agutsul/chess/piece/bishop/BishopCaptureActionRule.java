package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.action.AbstractCaptureLineActionRule;

class BishopCaptureActionRule<COLOR1 extends Color,
                              COLOR2 extends Color,
                              BISHOP extends BishopPiece<COLOR1>,
                              PIECE extends Piece<COLOR2>>
        extends AbstractCaptureLineActionRule<COLOR1, COLOR2, BISHOP, PIECE,
                                              PieceCaptureAction<COLOR1, COLOR2, BISHOP, PIECE>> {

    BishopCaptureActionRule(Board board, CapturePieceAlgo<COLOR1, BISHOP, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<COLOR1, COLOR2, BISHOP, PIECE>
            createAction(BISHOP bishop, PIECE piece, Line line) {

        return new PieceCaptureAction<COLOR1, COLOR2, BISHOP, PIECE>(bishop, piece, line);
    }
}