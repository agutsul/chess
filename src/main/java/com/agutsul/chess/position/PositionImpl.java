package com.agutsul.chess.position;

import com.agutsul.chess.exception.IllegalPositionException;

final class PositionImpl
        implements Position {

    private static final String INVALID_POSITION_MESSAGE = "Invalid position";

    private final int x;
    private final int y;
    private final String code;

    PositionImpl(int x, int y) {
        var code = Position.codeOf(x,y);
        if (code == null) {
            throw new IllegalPositionException(
                    String.format("%s [%d,%d]", INVALID_POSITION_MESSAGE, x, y)
            );
        }

        this.x = x;
        this.y = y;
        this.code = code;
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
        return code;
    }

    @Override
    public String toString() {
        return getCode();
    }
}