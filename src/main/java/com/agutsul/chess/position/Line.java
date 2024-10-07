package com.agutsul.chess.position;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class Line extends ArrayList<Position> implements Calculated {

    private static final long serialVersionUID = 1L;

    public Line(Line line1, Line line2) {
        this(getPositions(line1, line2));
    }

    public Line(List<Position> positions) {
        super(positions);
    }

    @Override
    public String toString() {
        return this.stream()
                .map(String::valueOf)
                .collect(joining(","));
    }

    private static List<Position> getPositions(Line line1, Line line2) {
        var positions = new ArrayList<Position>();
        positions.addAll(line1);
        positions.addAll(line2);
        return positions;
    }
}