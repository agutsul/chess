package com.agutsul.chess.action;

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
}