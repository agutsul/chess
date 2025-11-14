package com.agutsul.chess.board;

import static com.agutsul.chess.line.LineFactory.lineOf;
import static com.agutsul.chess.line.LineFactory.linesOf;
import static com.agutsul.chess.position.PositionFactory.positionOf;

import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.line.Line;
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