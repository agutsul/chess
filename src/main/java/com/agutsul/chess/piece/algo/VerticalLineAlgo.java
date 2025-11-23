package com.agutsul.chess.piece.algo;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.line.LineFactory;
import com.agutsul.chess.piece.Piece;

public final class VerticalLineAlgo<COLOR extends Color,
                                    PIECE extends Piece<COLOR>>
        extends AbstractLineAlgo<PIECE,Line> {

    public VerticalLineAlgo(Board board) {
        super(board);
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var currentPosition = piece.getPosition();
        var lines = Stream.of(calculate(currentPosition, 0, -1),
                              calculate(currentPosition, 0,  1)
                )
                .map(LineFactory::lineOf)
                .toList();

        return lines;
    }
}