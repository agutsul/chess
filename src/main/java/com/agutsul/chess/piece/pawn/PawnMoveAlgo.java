package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

class PawnMoveAlgo<COLOR extends Color,
                   PAWN extends PawnPiece<COLOR>>
        extends AbstractAlgo<PAWN,Position>
        implements MovePieceAlgo<COLOR,PAWN,Position> {

    protected final int step;

    PawnMoveAlgo(Board board, int step) {
        super(board);
        this.step = step;
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        return collect(calculate(pawn, this.step));
    }

    Optional<Position> calculate(PAWN pawn, int step) {
        var currentPosition = pawn.getPosition();
        return board.getPosition(
                currentPosition.x(),
                currentPosition.y() + step
        );
    }

    Collection<Position> collect(Optional<Position> calculated) {
        return Stream.of(calculated)
                .flatMap(Optional::stream)
                .filter(position -> board.isEmpty(position))
                .toList();
    }
}