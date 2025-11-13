package com.agutsul.chess.line;

import static com.agutsul.chess.line.LineFactory.lineOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.builder.Builder;

import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;

public final class LineBuilder implements Builder<Line> {

    private static final Comparator<Position> COMPARATOR = new PositionComparator();

    private final List<Position> positions = new ArrayList<>();

    public LineBuilder append(Position position) {
        this.positions.add(position);
        return this;
    }

    public LineBuilder append(Collection<Position> positions) {
        this.positions.addAll(positions);
        return this;
    }

    public LineBuilder sort() {
        this.positions.sort(COMPARATOR);
        return this;
    }

    public void reset() {
        this.positions.clear();
    }

    @Override
    public Line build() {
        try {
            return lineOf(this.positions.stream().distinct().toList());
        } finally {
            reset();
        }
    }
}