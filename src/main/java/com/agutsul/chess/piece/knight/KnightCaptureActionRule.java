package com.agutsul.chess.piece.knight;

import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.action.AbstractCapturePositionActionRule;

class KnightCaptureActionRule<COLOR1 extends Color,
                              COLOR2 extends Color,
                              KNIGHT extends KnightPiece<COLOR1>,
                              PIECE extends Piece<COLOR2>>
        extends AbstractCapturePositionActionRule<COLOR1,COLOR2,KNIGHT,PIECE,
                                                  PieceCaptureAction<COLOR1,COLOR2,KNIGHT,PIECE>> {

    KnightCaptureActionRule(Board board,
                            CapturePieceAlgo<COLOR1,KNIGHT,Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCaptureAction<COLOR1,COLOR2,KNIGHT,PIECE> createAction(KNIGHT knight,
                                                                          PIECE piece) {
        return new PieceCaptureAction<>(knight, piece);
    }
}