package com.agutsul.chess.line;

import static com.agutsul.chess.line.Line.COMMA_SEPARATOR;
import static com.agutsul.chess.position.Position.MAX;
import static com.agutsul.chess.position.Position.MIN;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.position.Position;

public enum LineFactory {
    INSTANCE;

    private static final Line EMPTY_LINE = new EmptyLine();

    private Map<String,Line> lines = Stream.of(lines(), diagonals())
            .flatMap(Collection::stream)
            .map(line -> Pair.of(createKey(line), line))
            .distinct()
            .collect(toMap(Pair::getKey, Pair::getValue));

    public Line create(Collection<Position> positions) {
        if (isEmpty(positions)) {
            return EMPTY_LINE;
        }

        var cachedLine = lines.get(createKey(positions));
        return isEmpty(cachedLine)
                ? new LineImpl(positions)
                : cachedLine;
    }

    public Line create(Line line1, Line line2) {
        return isEmpty(line1) && isEmpty(line2)
            ? EMPTY_LINE
            : new CompositeLine(line1, line2);
    }

    // creates composite line
    public static Line lineOf(Line line1, Line line2) {
        return INSTANCE.create(line1, line2);
    }

    // creates line based on provided positions
    // checks existing full lines before creation
    // new line is created for sub-line only
    public static Line lineOf(Collection<Position> positions) {
        return INSTANCE.create(positions);
    }

    // returns full line for specified positions if there is any
    public static Optional<Line> lineOf(Position position1, Position position2) {
        var positions = Stream.of(position1, position2)
                .filter(Objects::nonNull)
                .collect(toSet());

        if (positions.size() <= 1) {
            // unable to select line to return ( horizontal or vertical or diagonal )
            return Optional.empty();
        }

        return Stream.of(INSTANCE.lines.values())
                .flatMap(Collection::stream)
                .filter(line -> line.containsAll(positions))
                .findFirst();
    }

    // returns all full lines containing provided position
    public static Collection<Line> linesOf(Position position) {
        if (isNull(position)) {
            return emptyList();
        }

        return Stream.of(INSTANCE.lines.values())
                .flatMap(Collection::stream)
                .filter(line -> line.contains(position) && line.size() > 1)
                .distinct()
                .toList();
    }

    private static Collection<Line> lines() {
        var lines = new ArrayList<Line>();

        for (var i = MIN; i < MAX; i++) {
            var horizontalPositions = new ArrayList<Position>();
            var verticalPositions   = new ArrayList<Position>();

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

    private static final class EmptyLine extends AbstractLine {

        private static final long serialVersionUID = 1L;

        private EmptyLine() {
            super(emptyList());
        }

        @Override
        public boolean containsAny(Collection<Position> positions) {
            return false;
        }

        @Override
        public Collection<Position> intersection(Collection<Position> positions) {
            return emptyList();
        }

        @Override
        public Collection<Line> split(Position position) {
            return emptyList();
        }

        @Override
        public Line subLine(Position start, Position finish) {
            return lineOf(emptyList());
        }
    }
}