package com.agutsul.chess.position;

import static java.util.Objects.hash;

import java.util.Objects;

import com.agutsul.chess.Valuable;
import com.agutsul.chess.color.Color;

public final class ValuablePosition
        implements Position, Valuable<Integer> {

    private static final long serialVersionUID = 1L;

    private final Position position;
    private final Integer  value;

    public ValuablePosition(Position position, Integer value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public int x() {
        return position.x();
    }

    @Override
    public int y() {
        return position.y();
    }

    @Override
    public Color getColor() {
        return position.getColor();
    }

    @Override
    public String getCode() {
        return position.getCode();
    }

    @Override
    public int hashCode() {
        return hash(position.hashCode(), value.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ValuablePosition)) {
            return false;
        }

        var other = (ValuablePosition) obj;
        return Objects.equals(other.x(), x())
                && Objects.equals(other.y(), y())
                && Objects.equals(other.getValue(), getValue());
    }

    @Override
    public String toString() {
        return String.format("%s:%d", position, value);
    }
}