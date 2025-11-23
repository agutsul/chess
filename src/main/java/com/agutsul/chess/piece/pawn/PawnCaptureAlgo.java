package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnCaptureAlgo<COLOR extends Color,
                            PAWN extends PawnPiece<COLOR>>
        extends AbstractAlgo<PAWN,Position>
        implements CapturePieceAlgo<COLOR,PAWN,Position> {

    private final int step;

    PawnCaptureAlgo(Board board, int step) {
        super(board);
        this.step = step;
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        return Stream.of(calculate(pawn, 1), calculate(pawn, -1))
                .flatMap(Optional::stream)
                .toList();
    }

    Optional<Position> calculate(PAWN pawn, int step) {
        var currentPosition = pawn.getPosition();
        return board.getPosition(
                currentPosition.x() + step,
                currentPosition.y() + this.step
        );
    }
}