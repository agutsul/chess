package com.agutsul.chess.position;

import static com.agutsul.chess.position.Position.codeOf;

import com.agutsul.chess.exception.IllegalPositionException;

final class PositionImpl
        implements Position {

    private static final String INVALID_POSITION_MESSAGE = "Invalid position";

    private final int x;
    private final int y;
    private final String code;

    PositionImpl(int x, int y) {
        var code = codeOf(x,y);
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