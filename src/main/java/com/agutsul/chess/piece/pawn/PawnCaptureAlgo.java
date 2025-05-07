package com.agutsul.chess.piece.pawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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
        var currentPosition = pawn.getPosition();

        var nextPositions = new ArrayList<Optional<Position>>();
        nextPositions.add(board.getPosition(
                currentPosition.x() + 1,
                currentPosition.y() + this.step
            ));
        nextPositions.add(board.getPosition(
                currentPosition.x() - 1,
                currentPosition.y() + this.step
            ));

        return nextPositions.stream()
                .flatMap(Optional::stream)
                .toList();
    }
}