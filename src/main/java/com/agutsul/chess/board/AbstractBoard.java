package com.agutsul.chess.board;

import static com.agutsul.chess.line.LineFactory.lineOf;
import static com.agutsul.chess.line.LineFactory.linesOf;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractBoard
        implements Board, Observable {

    @Override
    public final Optional<Position> getPosition(String code) {
        return Optional.ofNullable(positionOf(code));
    }

    @Override
    public final Optional<Position> getPosition(int x, int y) {
        return Optional.ofNullable(positionOf(x, y));
    }

    @Override
    public final Optional<Line> getLine(Piece<?> piece1, Piece<?> piece2) {
        var line = Stream.of(getLine(piece1.getPosition(), piece2.getPosition()))
                .flatMap(Optional::stream)
                .filter(not(Collection::isEmpty))
                .map(fullLine -> fullLine.subLine(piece1.getPosition(), piece2.getPosition()))
                .findFirst();

        return line;
    }

    @Override
    public final Optional<Line> getLine(Position position1, Position position2) {
        return lineOf(position1, position2);
    }

    @Override
    public final Optional<Line> getLine(String position1, String position2) {
        return lineOf(positionOf(position1), positionOf(position2));
    }

    @Override
    public final Collection<Line> getLines(Position position) {
        return linesOf(position);
    }

    @Override
    public final Collection<Line> getLines(String position) {
        return linesOf(positionOf(position));
    }

    @Override
    public String toString() {
        return BoardFormatter.format(this);
    }
}