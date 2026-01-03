package com.agutsul.chess.line;

import static com.agutsul.chess.line.LineFactory.lineOf;
import static java.util.Collections.emptyList;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.position.Position;

abstract class AbstractLine
        extends ArrayList<Position>
        implements Line {

    private static final long serialVersionUID = 1L;

    AbstractLine(Collection<Position> positions) {
        super(positions);
    }

    @Override
    public Collection<Line> split(Position position) {
        var index = indexOf(position);
        if (index < 0) {
            return emptyList();
        }

        var size = size();
        return Stream.of(subList(0, Math.min(index + 1, size)), subList(index, size))
                .distinct()
                .map(LineFactory::lineOf)
                .toList();
    }

    @Override
    public Line subLine(Position start, Position finish) {
        var startIndex  = indexOf(start);
        var finishIndex = indexOf(finish);

        if (startIndex < 0 || finishIndex < 0) {
            return lineOf(emptyList());
        }

        if (startIndex <= finishIndex) {
            return lineOf(subList(startIndex, Math.min(finishIndex + 1, size())));
        }

        var positions = new ArrayList<>(this);
        return lineOf(positions.reversed()).subLine(start, finish);
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