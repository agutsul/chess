package com.agutsul.chess.piece.pawn;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

final class PawnBigMoveAlgo<COLOR extends Color,
                            PAWN extends PawnPiece<COLOR>>
        extends PawnMoveAlgo<COLOR,PAWN> {

    private final int initialLine;
    private final int bigStep;

    PawnBigMoveAlgo(Board board, int step, int initialLine) {
        super(board, step);

        this.initialLine = initialLine;
        this.bigStep = step * PawnPiece.BIG_STEP_MOVE;
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        var currentPosition = pawn.getPosition();
        if (currentPosition.y() != this.initialLine) {
            return emptyList();
        }

        if (Stream.of(super.calculate(pawn, this.step))
                .flatMap(Optional::stream)
                .noneMatch(position -> board.isEmpty(position))) {

            return emptyList();
        }

        return Stream.of(super.calculate(pawn, this.bigStep))
                .flatMap(Optional::stream)
                .filter(position -> board.isEmpty(position))
                .map(List::of)
                .findFirst()
                .orElse(emptyList());
    }
}