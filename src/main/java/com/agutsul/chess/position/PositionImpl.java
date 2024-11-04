package com.agutsul.chess.position;

import com.agutsul.chess.exception.IllegalPositionException;

final class PositionImpl
        implements Position {

    private final int x;
    private final int y;

    PositionImpl(int x, int y) {
        if (x < MIN || x >= MAX) {
            throw new IllegalPositionException("Invalid position x: " + x);
        }

        if (y < MIN || y >= MAX) {
            throw new IllegalPositionException("Invalid position y: " + y);
        }

        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    public String getCode() {
        return Position.codeOf(x, y);
    }

    @Override
    public String toString() {
        return getCode();
    }
}
