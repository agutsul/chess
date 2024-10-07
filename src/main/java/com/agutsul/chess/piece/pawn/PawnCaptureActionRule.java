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

class PawnCaptureActionRule<C1 extends Color,
                            C2 extends Color,
                            PAWN extends PawnPiece<C1>,
                            PIECE extends Piece<C2> & Capturable>
        extends AbstractCapturePositionActionRule<C1, C2, PAWN, PIECE,
                                                  PieceCaptureAction<C1, C2, PAWN, PIECE>> {

    PawnCaptureActionRule(Board board, CapturePieceAlgo<C1, PAWN, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<C1, C2, PAWN, PIECE>createAction(PAWN pawn, PIECE piece) {
        return new PieceCaptureAction<C1, C2, PAWN, PIECE>(pawn, piece);
    }
}