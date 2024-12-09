package com.agutsul.chess.impact;

import java.util.Objects;

public abstract class AbstractTargetImpact<SOURCE, TARGET>
        extends AbstractSourceImpact<SOURCE> {

    private final TARGET target;

    AbstractTargetImpact(Type type, SOURCE source, TARGET target) {
        super(type, source);
        this.target = target;
    }

    public TARGET getTarget() {
        return target;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(target);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof AbstractTargetImpact)) {
            return false;
        }

        var other = (AbstractTargetImpact<?,?>) obj;
        return Objects.equals(getTarget(), other.getTarget());
    }
}