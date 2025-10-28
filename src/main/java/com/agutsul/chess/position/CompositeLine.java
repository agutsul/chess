package com.agutsul.chess.position;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

final class CompositeLine extends ArrayList<Position> implements Line {

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
        return left.containsAny(positions)
                || right.containsAny(positions);
    }

    @Override
    public String toString() {
        return join(this, COMMA_SEPARATOR);
    }
}