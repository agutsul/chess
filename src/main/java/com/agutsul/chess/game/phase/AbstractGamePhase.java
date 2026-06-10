package com.agutsul.chess.game.phase;

import com.agutsul.chess.color.Color;

abstract class AbstractGamePhase
        implements GamePhase {

    private final Type type;
    private final Color color;

    AbstractGamePhase(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", getColor(), getType());
    }
}
