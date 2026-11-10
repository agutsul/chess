package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnCaptureAlgo<COLOR extends Color,
                            PAWN  extends PawnPiece<COLOR>>
        extends AbstractPawnAlgo<COLOR,PAWN>
        implements CapturePieceAlgo<COLOR,PAWN,Position> {

    PawnCaptureAlgo(Board board, int step) {
        super(board, step);
    }

    @Override
    public Collection<Position> calculate(Position position) {
        return Stream.of(calculate(position, 1), calculate(position, -1))
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    Optional<Position> calculate(Position position, int step) {
        return board.getPosition(position.x() + step, position.y() + this.step);
    }
}