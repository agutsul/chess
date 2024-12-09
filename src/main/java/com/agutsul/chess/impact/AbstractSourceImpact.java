package com.agutsul.chess.impact;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(source, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof AbstractSourceImpact)) {
            return false;
        }

        var other = (AbstractSourceImpact<?>) obj;
        return Objects.equals(getSource(), other.getSource())
                && Objects.equals(getType(), other.getType());
    }
}