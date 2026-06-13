package com.agutsul.chess.game.result;

abstract class AbstractGameResult
        implements GameResult {

    private final Type type;

    AbstractGameResult(Type type) {
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