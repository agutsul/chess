package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.action.AbstractCapturePositionActionRule;

class PawnCaptureActionRule<COLOR1 extends Color,
                            COLOR2 extends Color,
                            PAWN extends PawnPiece<COLOR1>,
                            PIECE extends Piece<COLOR2> & Capturable>
        extends AbstractCapturePositionActionRule<COLOR1, COLOR2, PAWN, PIECE,
                                                  PieceCaptureAction<COLOR1, COLOR2, PAWN, PIECE>> {

    PawnCaptureActionRule(Board board, CapturePieceAlgo<COLOR1, PAWN, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<COLOR1, COLOR2, PAWN, PIECE>
            createAction(PAWN pawn, PIECE piece) {

        return new PieceCaptureAction<COLOR1, COLOR2, PAWN, PIECE>(pawn, piece);
    }
}