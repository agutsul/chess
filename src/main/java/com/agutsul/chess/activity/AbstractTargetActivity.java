package com.agutsul.chess.activity;

import java.util.Objects;

public abstract class AbstractTargetActivity<TYPE extends Activity.Type,SOURCE,TARGET>
        extends AbstractSourceActivity<TYPE,SOURCE> {

    private final TARGET target;

    protected AbstractTargetActivity(TYPE type,
                                     SOURCE source,
                                     TARGET target) {
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

        if (!(obj instanceof AbstractTargetActivity)) {
            return false;
        }

        var other = (AbstractTargetActivity<?,?,?>) obj;
        return Objects.equals(getTarget(), other.getTarget());
    }
}