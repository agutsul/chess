package com.agutsul.chess.action;

import java.util.Objects;

public abstract class AbstractTargetAction<SOURCE,TARGET>
        extends AbstractSourceAction<SOURCE> {

    private final TARGET target;

    AbstractTargetAction(Type type, SOURCE source, TARGET target) {
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

        if (!(obj instanceof AbstractTargetAction)) {
            return false;
        }

        var other = (AbstractTargetAction<?,?>) obj;
        return Objects.equals(getTarget(), other.getTarget());
    }
}