package com.agutsul.chess.piece.algo;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.RookPiece;

public final class KingSideCastlingAlgo<COLOR extends Color,
                                        KING  extends KingPiece<COLOR>,
                                        ROOK  extends RookPiece<COLOR>>
        extends AbstractCastlingAlgo<COLOR,KING,ROOK> {

    public KingSideCastlingAlgo(Board board, int castlingLine) {
        super(board, castlingLine);
    }

    @Override
    boolean isAllEmptyBetween(KING king, ROOK rook) {
        var rookPosition = rook.getPosition();
        var kingPosition = king.getPosition();

        int iterations = 0;
        for (int i = kingPosition.x() + 1; i < rookPosition.x(); i++) {
            var optionalPosition = board.getPosition(i, rookPosition.y());
            if (optionalPosition.isEmpty()
                    || !board.isEmpty(optionalPosition.get())) {

                return false;
            }
            iterations++;
        }

        return iterations == 2;
    }

    @Override
    boolean isAnyAttackedBetween(KING king, ROOK rook) {
        var rookPosition = rook.getPosition();
        var kingPosition = king.getPosition();
        var attackerColor = king.getColor().invert();

        for (int i = kingPosition.x() + 1; i < rookPosition.x(); i++) {
            var optionalPosition = board.getPosition(i, rookPosition.y());
            if (optionalPosition.isEmpty()
                    || board.isAttacked(optionalPosition.get(), attackerColor)) {

                return true;
            }
        }

        return false;
    }
}