package com.agutsul.chess.line;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;

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

    @Override
    public int hashCode() {
        return Objects.hash(getLeft(), getRight());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof CompositeLine)) {
            return false;
        }

        var other = (CompositeLine) obj;
        return Objects.equals(getLeft(), other.getLeft())
                && Objects.equals(getRight(), other.getRight());
    }
}