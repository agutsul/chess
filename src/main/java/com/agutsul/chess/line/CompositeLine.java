package com.agutsul.chess.line;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.position.Position;

final class CompositeLine extends AbstractLine {

    private static final long serialVersionUID = 1L;

    private final Line left;
    private final Line right;

    CompositeLine(Line left, Line right) {
        super(new LineBuilder().append(left).append(right).sort().build());

        this.left  = left;
        this.right = right;
    }

    public Line getLeft() {
        return left;
    }

    public Line getRight() {
        return right;
    }

    @Override
    public boolean containsAny(Collection<Position> positions) {
        return Stream.of(left, right)
                .anyMatch(line -> line.containsAny(positions));
    }

    @Override
    public Collection<Position> intersection(Collection<Position> positions) {
        return Stream.of(left, right)
                .map(line -> line.intersection(positions))
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }
}