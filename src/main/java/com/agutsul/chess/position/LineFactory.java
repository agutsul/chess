package com.agutsul.chess.position;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;

public enum LineFactory {
    INSTANCE;

    private static final Comparator<Position> COMPARATOR = new PositionComparator();

    public static Line createLine(Position position, Collection<Line> lines) {
        var positions = new ArrayList<Position>();
        positions.add(position);

        lines.forEach(line -> positions.addAll(line));

        sort(positions, COMPARATOR);
        return createLine(positions);
    }

    public static Line createLine(Line line1, Line line2) {
        return createLine(Stream.of(line1, line2).flatMap(Collection::stream).toList());
    }

    public static Line createLine(List<Position> positions) {
        return new LineImpl(positions);
    }

    public static Line createLine(Board board, Position current, int xStep, int yStep) {
        return createLine(INSTANCE.calculateLine(board, current,
                new ArrayList<Position>(),
                xStep, yStep
        ));
    }

    private List<Position> calculateLine(Board board, Position current,
                                         List<Position> positions, int x, int y) {

        var optionalNext = board.getPosition(current.x() + x, current.y() + y);
        if (optionalNext.isEmpty()) {
            return positions;
        }

        var nextPosition = optionalNext.get();
        positions.add(nextPosition);

        return calculateLine(board, nextPosition, positions, x, y);
    }
}