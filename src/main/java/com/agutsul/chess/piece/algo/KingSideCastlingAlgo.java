package com.agutsul.chess.piece.algo;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;

public final class KingSideCastlingAlgo<COLOR extends Color,
                                        KING  extends KingPiece<COLOR>,
                                        ROOK  extends RookPiece<COLOR>>
        extends AbstractCastlingAlgo<COLOR,KING,ROOK> {

    private static final int EMPTY_POSITIONS = 2;

    public KingSideCastlingAlgo(Board board, COLOR color, int castlingLine) {
        super(Castlingable.Side.KING, board, color, castlingLine);
    }

    @Override
    public boolean isAnyAttackedBetween(Position kingPosition, Position rookPosition) {
        var attackerColor = getColor().invert();

        for (int i = kingPosition.x() + 1; i < rookPosition.x(); i++) {
            var optionalPosition = board.getPosition(i, rookPosition.y());
            if (optionalPosition.isEmpty()
                    || board.isAttacked(optionalPosition.get(), attackerColor)) {

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAllEmptyBetween(Position kingPosition, Position rookPosition) {
        int counter = 0;
        for (int i = kingPosition.x() + 1; i < rookPosition.x(); i++) {
            var optionalPosition = board.getPosition(i, rookPosition.y());
            if (optionalPosition.isEmpty()
                    || !board.isEmpty(optionalPosition.get())) {

                return false;
            }
            counter++;
        }

        return counter == EMPTY_POSITIONS;
    }
}