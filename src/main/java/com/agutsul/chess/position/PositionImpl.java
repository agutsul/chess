package com.agutsul.chess.position;

final class PositionImpl
        implements Position {

    private final int x;
    private final int y;

    PositionImpl(int x, int y) {
        if (x < MIN || x >= MAX) {
            throw new IllegalArgumentException("Invalid position x: " + x);
        }

        if (y < MIN || y >= MAX) {
            throw new IllegalArgumentException("Invalid position y: " + y);
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
        return String.format("%s%d", LABELS[x], y + 1);
    }

    @Override
    public String toString() {
        return getCode();
    }
}
