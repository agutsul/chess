package com.agutsul.chess.board;

import java.util.Optional;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

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
    public String toString() {
        return BoardFormatter.format(this);
    }

    static Position positionOf(int x, int y) {
        return PositionFactory.INSTANCE.createPosition(x, y);
    }

    static Position positionOf(String code) {
        return PositionFactory.INSTANCE.createPosition(code);
    }
}