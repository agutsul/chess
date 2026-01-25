package com.agutsul.chess.activity;

import static java.util.Objects.hash;
import static java.util.Objects.isNull;

import java.util.Objects;

public abstract class AbstractSourceActivity<TYPE extends Activity.Type,SOURCE>
        implements Activity<TYPE,SOURCE> {

    private final TYPE type;
    private final SOURCE source;

    protected AbstractSourceActivity(TYPE type,
                                     SOURCE source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public TYPE getType() {
        return type;
    }

    @Override
    public SOURCE getSource() {
        return source;
    }

    @Override
    public int hashCode() {
        return hash(source, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (isNull(obj)) {
            return false;
        }

        if (!(obj instanceof AbstractSourceActivity)) {
            return false;
        }

        var other = (AbstractSourceActivity<?,?>) obj;
        return Objects.equals(getSource(), other.getSource())
                && Objects.equals(getType(), other.getType());
    }
}