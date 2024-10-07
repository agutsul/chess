package com.agutsul.chess.impact;

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
}