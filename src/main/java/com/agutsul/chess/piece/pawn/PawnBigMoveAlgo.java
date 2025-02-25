package com.agutsul.chess.piece.pawn;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

final class PawnBigMoveAlgo<COLOR extends Color,
                            PAWN extends PawnPiece<COLOR>>
        extends PawnMoveAlgo<COLOR,PAWN> {

    private final int initialLine;

    PawnBigMoveAlgo(Board board, int step, int initialLine) {
        super(board, step * PawnPiece.BIG_STEP_MOVE);
        this.initialLine = initialLine;
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        var currentPosition = pawn.getPosition();
        return currentPosition.y() == this.initialLine
                ? super.calculate(pawn)
                : emptyList();
    }
}