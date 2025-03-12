package com.agutsul.chess.piece.king;

import java.util.Optional;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractCapturePositionActionRule;

class KingCaptureActionRule<COLOR1 extends Color,
                            COLOR2 extends Color,
                            KING extends KingPiece<COLOR1>,
                            PIECE extends Piece<COLOR2>>
        extends AbstractCapturePositionActionRule<COLOR1,COLOR2,KING,PIECE,
                                                  PieceCaptureAction<COLOR1,COLOR2,KING,PIECE>> {

    KingCaptureActionRule(Board board,
                          CapturePieceAlgo<COLOR1,KING,Position> algo) {
        super(board, algo);
    }

    @Override
    protected Optional<PIECE> getCapturePiece(KING attacker, Position position) {
        var optionalPiece = super.getCapturePiece(attacker, position);

        if (optionalPiece.isPresent()) {
            var piece = optionalPiece.get();
            if (((Protectable) piece).isProtected()) {
                return Optional.empty();
            }
        }

        return optionalPiece;
    }

    @Override
    protected PieceCaptureAction<COLOR1,COLOR2,KING,PIECE>
            createAction(KING king, PIECE piece) {

        return new PieceCaptureAction<>(king, piece);
    }
}