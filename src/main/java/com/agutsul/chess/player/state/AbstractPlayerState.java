package com.agutsul.chess.player.state;

abstract class AbstractPlayerState implements PlayerState {

    private final Type type;

    AbstractPlayerState(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.name();
    }
}