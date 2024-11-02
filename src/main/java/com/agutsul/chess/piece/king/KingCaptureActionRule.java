package com.agutsul.chess.piece.king;

import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.action.AbstractCapturePositionActionRule;

class KingCaptureActionRule<COLOR1 extends Color,
                            COLOR2 extends Color,
                            KING extends KingPiece<COLOR1>,
                            PIECE extends Piece<COLOR2> & Capturable>
        extends AbstractCapturePositionActionRule<COLOR1, COLOR2, KING, PIECE,
                                                  PieceCaptureAction<COLOR1, COLOR2, KING, PIECE>> {

    KingCaptureActionRule(Board board, CapturePieceAlgo<COLOR1, KING, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<COLOR1, COLOR2, KING, PIECE>
            createAction(KING king, PIECE piece) {

        return new PieceCaptureAction<COLOR1, COLOR2, KING, PIECE>(king, piece);
    }
}