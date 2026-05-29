package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.AbstractPositionAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnCaptureAlgo<COLOR extends Color,
                            PAWN  extends PawnPiece<COLOR>>
        extends AbstractPositionAlgo<PAWN,Position>
        implements CapturePieceAlgo<COLOR,PAWN,Position> {

    private final int step;

    PawnCaptureAlgo(Board board, int step) {
        super(board);
        this.step = step;
    }

    @Override
    public Collection<Position> calculate(Position position) {
        return Stream.of(calculate(position, 1), calculate(position, -1))
                .flatMap(Optional::stream)
                .toList();
    }

    Optional<Position> calculate(Position position, int step) {
        return board.getPosition(position.x() + step, position.y() + this.step);
    }
}