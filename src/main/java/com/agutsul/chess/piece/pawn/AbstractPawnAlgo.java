package com.agutsul.chess.piece.pawn;

import java.util.Optional;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.AbstractPositionAlgo;
import com.agutsul.chess.position.Position;

abstract class AbstractPawnAlgo<COLOR extends Color,
                                PAWN  extends PawnPiece<COLOR>>
        extends AbstractPositionAlgo<PAWN,Position> {

    final int step;

    AbstractPawnAlgo(Board board, int step) {
        super(board);
        this.step = step;
    }

    abstract Optional<Position> calculate(Position position, int step);
}