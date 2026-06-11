package com.agutsul.chess.position;

import com.agutsul.chess.color.Color;

final class CentralPosition implements Position {

    private static final long serialVersionUID = -91693129392372764L;

    private final Position position;

    CentralPosition(Position position) {
        this.position = position;
    }

    @Override
    public int x() {
        return this.position.x();
    }

    @Override
    public int y() {
        return this.position.y();
    }

    @Override
    public Color getColor() {
        return this.position.getColor();
    }

    @Override
    public String getCode() {
        return this.position.getCode();
    }

    @Override
    public String toString() {
        return this.position.toString();
    }

    @Override
    public int hashCode() {
        return this.position.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.position.equals(obj);
    }
}