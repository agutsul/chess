package com.agutsul.chess.piece.knight;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.action.AbstractCapturePositionActionRule;

class KnightCaptureActionRule<C1 extends Color,
                              C2 extends Color,
                              KNIGHT extends KnightPiece<C1>,
                              PIECE extends Piece<C2> & Capturable>
        extends AbstractCapturePositionActionRule<C1, C2, KNIGHT, PIECE,
                                                  PieceCaptureAction<C1, C2, KNIGHT, PIECE>> {

    KnightCaptureActionRule(Board board, CapturePieceAlgo<C1, KNIGHT, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<C1, C2, KNIGHT, PIECE> createAction(KNIGHT knight, PIECE piece) {
        return new PieceCaptureAction<C1, C2, KNIGHT, PIECE>(knight, piece);
    }
}