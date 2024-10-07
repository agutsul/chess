package com.agutsul.chess.action;

public abstract class AbstractSourceAction<SOURCE>
        implements Action<SOURCE> {

    private final Type type;
    private final SOURCE source;

    AbstractSourceAction(Type type, SOURCE source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public SOURCE getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getCode();
    }
}