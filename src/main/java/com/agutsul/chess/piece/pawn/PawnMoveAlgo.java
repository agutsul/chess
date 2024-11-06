package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnMoveAlgo<COLOR extends Color,
                         PAWN extends PawnPiece<COLOR>>
        extends AbstractAlgo<PAWN, Position>
        implements MovePieceAlgo<COLOR, PAWN, Position> {

    private final int step;
    private final int initialLine;

    PawnMoveAlgo(Board board, int step, int initialLine) {
        super(board);
        this.step = step;
        this.initialLine = initialLine;
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        var currentPosition = pawn.getPosition();

        var nextPositions = new ArrayList<Optional<Position>>();
        nextPositions.add(board.getPosition(
                currentPosition.x(),
                currentPosition.y() + this.step
            ));

        if (currentPosition.y() == initialLine) {
            // move for 2 cells available for the first time when piece not yet moved
            nextPositions.add(board.getPosition(
                    currentPosition.x(),
                    currentPosition.y() + this.step * 2
            ));
        }

        return nextPositions.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }
}