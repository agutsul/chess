package com.agutsul.chess.line;

import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.position.Position;

abstract class AbstractLine
        extends ArrayList<Position>
        implements Line {

    private static final long serialVersionUID = 1L;

    AbstractLine(Collection<Position> positions) {
        super(positions);
    }

    @Override
    public String toString() {
        return join(this, COMMA_SEPARATOR);
    }

    @Override
    public int hashCode() {
        return range(0, size())
                .mapToObj(this::get)
                .mapToInt(Position::hashCode)
                .sum();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Line)) {
            return false;
        }

        var other = (Line) obj;
        if (size() != other.size()) {
            return false;
        }

        return containsAll(other)
                && other.containsAll(this);
    }
}