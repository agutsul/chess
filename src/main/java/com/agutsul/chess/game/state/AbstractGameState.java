package com.agutsul.chess.game.state;

abstract class AbstractGameState
        implements GameState {

    private final Type type;

    AbstractGameState(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }
}