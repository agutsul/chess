package com.agutsul.chess.line;

import static com.agutsul.chess.line.Line.COMMA_SEPARATOR;
import static com.agutsul.chess.position.Position.MAX;
import static com.agutsul.chess.position.Position.MIN;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.position.Position;

public enum LineFactory {
    INSTANCE;

    private Map<String,Line> lines = Stream.of(lines(), diagonals())
            .flatMap(Collection::stream)
            .map(line -> Pair.of(createKey(line), line))
            .distinct()
            .collect(toMap(Pair::getKey, Pair::getValue));

    public static Line createLine(Line line1, Line line2) {
        return new CompositeLine(line1, line2);
    }

    public static Line createLine(Collection<Position> positions) {
        var line = INSTANCE.lines.get(createKey(positions));
        if (line != null) {
            return line;
        }

        return new LineImpl(positions);
    }

    public static Line createLine(Board board, Position current, int xStep, int yStep) {
        return createLine(calculate(board, current, xStep, yStep, new ArrayList<Position>()));
    }

    private static List<Position> calculate(Board board, Position current, int x, int y,
                                            List<Position> positions) {

        var optionalNext = board.getPosition(current.x() + x, current.y() + y);
        if (optionalNext.isEmpty()) {
            return positions;
        }

        var nextPosition = optionalNext.get();
        positions.add(nextPosition);

        return calculate(board, nextPosition, x, y, positions);
    }

    private static Collection<Line> lines() {
        var lines = new ArrayList<Line>();
        for (var i = MIN; i < MAX; i++) {
            var horizontalPositions = new ArrayList<Position>();
            var verticalPositions = new ArrayList<Position>();

            for (var j = MIN; j < MAX; j++) {
                horizontalPositions.add(positionOf(j,i));
                verticalPositions.add(positionOf(i,j));
            }

            lines.add(new LineImpl(horizontalPositions));
            lines.add(new LineImpl(verticalPositions));
        }

        return lines;
    }

    private static Collection<Line> diagonals() {
        var lines = new ArrayList<Line>();

        // Iterate through diagonals: Diagonals from bottom-left to top-right
        for (var sum = MIN; sum <= 2 * MAX - 2; sum++) {
            var diagonalPositions1 = new ArrayList<Position>();
            var diagonalPositions2 = new ArrayList<Position>();

            for (var i = MIN; i < MAX; i++) {
                var j = sum - i;
                if (j >= MIN && j < MAX) {
                    diagonalPositions1.add(positionOf(i,j));
                    diagonalPositions2.add(positionOf(j,i));
                }
            }

            lines.add(new LineImpl(diagonalPositions1));
            lines.add(new LineImpl(diagonalPositions2));
        }

        // Iterate through diagonals: Diagonals from top-left to bottom-right
        for (var diff = -(MAX - 1); diff < MAX; diff++) {
            var diagonalPositions1 = new ArrayList<Position>();
            var diagonalPositions2 = new ArrayList<Position>();

            for (var i = MIN; i < MAX; i++) {
                var j = i - diff;
                if (j >= MIN && j < MAX) {
                    diagonalPositions1.add(positionOf(i,j));
                    diagonalPositions2.add(positionOf(j,i));
                }
            }

            lines.add(new LineImpl(diagonalPositions1));
            lines.add(new LineImpl(diagonalPositions2));
        }

        return lines;
    }

    private static String createKey(Collection<Position> positions) {
        return Stream.of(positions)
                .flatMap(Collection::stream)
                .map(String::valueOf)
                .collect(joining(COMMA_SEPARATOR));
    }
}