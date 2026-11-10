package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

class PawnMoveAlgo<COLOR extends Color,
                   PAWN  extends PawnPiece<COLOR>>
        extends AbstractPawnAlgo<COLOR,PAWN>
        implements MovePieceAlgo<COLOR,PAWN,Position> {

    PawnMoveAlgo(Board board, int step) {
        super(board, step);
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        return collect(calculate(pawn, this.step));
    }

    @Override
    public Collection<Position> calculate(Position position) {
        return collect(calculate(position, this.step));
    }

    Optional<Position> calculate(PAWN pawn, int step) {
        return calculate(pawn.getPosition(), step);
    }

    @Override
    Optional<Position> calculate(Position position, int step) {
        return board.getPosition(position.x(), position.y() + step);
    }

    Collection<Position> collect(Optional<Position> calculated) {
        return Stream.of(calculated)
                .flatMap(Optional::stream)
                .filter(position -> board.isEmpty(position))
                .toList();
    }
}