package com.agutsul.chess.piece.algo;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Lineable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class LinePositionAlgoAdapter<COLOR extends Color,
                                           PIECE extends Piece<COLOR> & Lineable>
        implements Algo<PIECE,Collection<Position>> {

    private final Algo<PIECE,Collection<Line>> algo;

    public LinePositionAlgoAdapter(Algo<PIECE,Collection<Line>> algo) {
        this.algo = algo;
    }

    @Override
    public Collection<Position> calculate(PIECE piece) {
        return Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream) // unwrap calculated lines
                .flatMap(Collection::stream) // unwrap line positions
                .toList();
    }
}