package com.agutsul.chess.piece.queen;

import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.action.AbstractCaptureLineActionRule;

class QueenCaptureActionRule<COLOR1 extends Color,
                             COLOR2 extends Color,
                             QUEEN extends QueenPiece<COLOR1>,
                             PIECE extends Piece<COLOR2>>
        extends AbstractCaptureLineActionRule<COLOR1, COLOR2, QUEEN, PIECE,
                                              PieceCaptureAction<COLOR1, COLOR2, QUEEN, PIECE>> {

    QueenCaptureActionRule(Board board, CapturePieceAlgo<COLOR1, QUEEN, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<COLOR1, COLOR2, QUEEN, PIECE>
            createAction(QUEEN queen, PIECE piece, Line line) {

        return new PieceCaptureAction<COLOR1, COLOR2, QUEEN, PIECE>(queen, piece, line);
    }
}