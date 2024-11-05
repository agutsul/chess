package com.agutsul.chess.board;

import java.util.Optional;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

public abstract class AbstractBoard
        implements Board, Observable {

    @Override
    public final Optional<Position> getPosition(String code) {
        var position = PositionFactory.INSTANCE.createPosition(code);
        return Optional.ofNullable(position);
    }

    @Override
    public final Optional<Position> getPosition(int x, int y) {
        var position = PositionFactory.INSTANCE.createPosition(x, y);
        return Optional.ofNullable(position);
    }

    @Override
    public String toString() {
        return BoardFormatter.format(this);
    }
}