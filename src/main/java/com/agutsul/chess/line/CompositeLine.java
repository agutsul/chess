package com.agutsul.chess.line;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;

final class CompositeLine extends AbstractLine {

    private static final Comparator<Position> COMPARATOR = new PositionComparator();

    private static final long serialVersionUID = 1L;

    private final Line left;
    private final Line right;

    CompositeLine(Line left, Line right) {
        super(Stream.of(left, right)
                .flatMap(Collection::stream)
                .distinct()
                .sorted(COMPARATOR)
                .toList()
        );

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