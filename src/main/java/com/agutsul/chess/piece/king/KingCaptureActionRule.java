package com.agutsul.chess.piece.king;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
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
    protected Collection<PieceCaptureAction<COLOR1,COLOR2,KING,PIECE>>
            createActions(KING king, Collection<Calculated> next) {

        var actions = new ArrayList<PieceCaptureAction<COLOR1,COLOR2,KING,PIECE>>();
        for (var position : next) {
            var optionalPiece = board.getPiece((Position) position);
            if (optionalPiece.isEmpty()) {
                continue;
            }

            @SuppressWarnings("unchecked")
            var piece = (PIECE) optionalPiece.get();
            if (king.getColor() == piece.getColor()) {
                continue;
            }

            if (((Protectable) piece).isProtected()) {
                continue;
            }

            actions.add(createAction(king, piece));
        }

        return actions;
    }

    @Override
    protected PieceCaptureAction<COLOR1,COLOR2,KING,PIECE>
            createAction(KING king, PIECE piece) {

        return new PieceCaptureAction<>(king, piece);
    }
}