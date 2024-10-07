package com.agutsul.chess.impact;

public abstract class AbstractSourceImpact<SOURCE>
        implements Impact<SOURCE> {

    private final Type type;
    private final SOURCE source;

    AbstractSourceImpact(Type type, SOURCE source) {
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