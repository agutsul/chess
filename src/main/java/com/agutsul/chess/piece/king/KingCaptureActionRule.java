package com.agutsul.chess.piece.king;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.action.AbstractCapturePositionActionRule;

class KingCaptureActionRule<C1 extends Color,
                            C2 extends Color,
                            KING extends KingPiece<C1>,
                            PIECE extends Piece<C2> & Capturable>
        extends AbstractCapturePositionActionRule<C1, C2, KING, PIECE,
                                                  PieceCaptureAction<C1, C2, KING, PIECE>> {

    KingCaptureActionRule(Board board, CapturePieceAlgo<C1, KING, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<C1, C2, KING, PIECE> createAction(KING king, PIECE piece) {
        return new PieceCaptureAction<C1, C2, KING, PIECE>(king, piece);
    }
}