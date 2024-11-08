package com.agutsul.chess.board;

import static com.agutsul.chess.position.PositionFactory.positionOf;

import java.util.Optional;

import com.agutsul.chess.event.Observable;
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
    public String toString() {
        return BoardFormatter.format(this);
    }
}