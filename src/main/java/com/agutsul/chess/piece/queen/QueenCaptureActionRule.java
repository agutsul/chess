package com.agutsul.chess.piece.queen;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.action.AbstractCaptureLineActionRule;

class QueenCaptureActionRule<C1 extends Color,
                             C2 extends Color,
                             QUEEN extends QueenPiece<C1>,
                             PIECE extends Piece<C2> & Capturable>
        extends AbstractCaptureLineActionRule<C1, C2, QUEEN, PIECE,
                                              PieceCaptureAction<C1, C2, QUEEN, PIECE>> {

    QueenCaptureActionRule(Board board, CapturePieceAlgo<C1, QUEEN, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<C1, C2, QUEEN, PIECE>
            createAction(QUEEN queen, PIECE piece, Line line) {

        return new PieceCaptureAction<C1, C2, QUEEN, PIECE>(queen, piece, line);
    }
}