package com.agutsul.chess.position;

import static com.agutsul.chess.position.Position.codeOf;
import static java.util.Objects.hash;
import static java.util.Objects.isNull;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalPositionException;

final class PositionImpl
        implements Position {

    private static final long serialVersionUID = 4419962722407474005L;

    private static final String INVALID_POSITION_MESSAGE = "Invalid position";

    private final int x;
    private final int y;
    private final String code;
    private final Color color;

    PositionImpl(int x, int y) {
        var code = codeOf(x,y);
        if (isNull(code)) {
            throw new IllegalPositionException(
                    String.format("%s [%d,%d]", INVALID_POSITION_MESSAGE, x, y)
            );
        }

        this.code = code;

        this.x = x;
        this.y = y;

        this.color = (x + y) % 2 == 0 ? Colors.BLACK : Colors.WHITE;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public Color getColor() {
        return color;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return getCode();
    }

    @Override
    public int hashCode() {
        return hash(x(), y());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Position)) {
            return false;
        }

        var other = (Position) obj;
        return x() == other.x() && y() == other.y();
    }
}