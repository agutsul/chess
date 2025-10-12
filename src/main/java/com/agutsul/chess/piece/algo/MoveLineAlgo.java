package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public final class MoveLineAlgo<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Movable>
        extends AbstractAlgo<PIECE,Line>
        implements MovePieceAlgo<COLOR,PIECE,Line> {

    private final MovePieceAlgo<COLOR,PIECE,Line> algo;

    public MoveLineAlgo(Board board,
                        MovePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var moveLines = new ArrayList<Line>();
        for (var line : algo.calculate(piece)) {
            var movePositions = new ArrayList<Position>();
            for (var position : line) {
                if (!board.isEmpty(position)) {
                    break;
                }

                movePositions.add(position);
            }

            if (!movePositions.isEmpty()) {
                moveLines.add(new Line(movePositions));
            }
        }

        return moveLines;
    }
}