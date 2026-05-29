package com.agutsul.chess.piece.algo;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Lineable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.line.LineFactory;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class HorizontalLineAlgo<COLOR extends Color,
                                      PIECE extends Piece<COLOR> & Lineable>
        extends AbstractLineAlgo<PIECE,Line> {

    public HorizontalLineAlgo(Board board) {
        super(board);
    }

    @Override
    public Collection<Line> calculate(Position position) {
        var lines = Stream.of(calculate(position,  1, 0),
                              calculate(position, -1, 0)
                )
                .map(LineFactory::lineOf)
                .filter(not(Line::isEmpty))
                .toList();

        return lines;
    }
}