package com.agutsul.chess.line;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;

public enum LineFactory {
    INSTANCE;

    private static final Comparator<Position> COMPARATOR = new PositionComparator();

    public static Line createLine(Position position, Line line) {
        var positions = new ArrayList<Position>(line);
        positions.add(position);

        sort(positions, COMPARATOR);
        return createLine(positions);
    }

    public static Line createLine(Line line1, Line line2) {
        return new CompositeLine(line1, line2);
    }

    public static Line createLine(Collection<Position> positions) {
        return new LineImpl(positions);
    }

    public static Line createLine(Board board, Position current, int xStep, int yStep) {
        var positions = INSTANCE.calculate(board, current,
                new ArrayList<Position>(),
                xStep, yStep
        );

        return createLine(positions);
    }

    private List<Position> calculate(Board board, Position current,
                                     List<Position> positions, int x, int y) {

        var optionalNext = board.getPosition(current.x() + x, current.y() + y);
        if (optionalNext.isEmpty()) {
            return positions;
        }

        var nextPosition = optionalNext.get();
        positions.add(nextPosition);

        return calculate(board, nextPosition, positions, x, y);
    }
}