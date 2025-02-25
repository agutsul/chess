package com.agutsul.chess.piece.pawn;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

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

    private final int step;

    PawnMoveAlgo(Board board, int step) {
        super(board);
        this.step = step;
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        var currentPosition = pawn.getPosition();

        var nextPosition = board.getPosition(
                currentPosition.x(),
                currentPosition.y() + this.step
        );

        return nextPosition.isPresent()
                ? List.of(nextPosition.get())
                : emptyList();
    }
}