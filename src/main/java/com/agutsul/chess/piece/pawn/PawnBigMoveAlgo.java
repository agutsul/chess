package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.BigMovable.BIG_STEP_MOVE;
import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.BigMovePieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnBigMoveAlgo<COLOR extends Color,
                            PAWN  extends PawnPiece<COLOR>>
        extends PawnMoveAlgo<COLOR,PAWN>
        implements BigMovePieceAlgo<COLOR,PAWN,Position> {

    private final int initialLine;

    PawnBigMoveAlgo(Board board, int step, int initialLine) {
        super(board, step);
        this.initialLine = initialLine;
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        return calculate(pawn.getPosition());
    }

    @Override
    public Collection<Position> calculate(Position position) {
        if (position.y() != this.initialLine) {
            return emptyList();
        }

        if (super.calculate(position).isEmpty()) {
            return emptyList();
        }

        return collect(super.calculate(position, this.step * BIG_STEP_MOVE));
    }
}