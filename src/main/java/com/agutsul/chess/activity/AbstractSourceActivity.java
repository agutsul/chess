package com.agutsul.chess.activity;

import java.util.Objects;

public abstract class AbstractSourceActivity<SOURCE>
        implements Activity<SOURCE> {

    private final Activity.Type type;
    private final SOURCE source;

    protected AbstractSourceActivity(Activity.Type type,
                                     SOURCE source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public Activity.Type getType() {
        return type;
    }

    @Override
    public SOURCE getSource() {
        return source;
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

        if (!(obj instanceof AbstractSourceActivity)) {
            return false;
        }

        var other = (AbstractSourceActivity<?>) obj;
        return Objects.equals(getSource(), other.getSource())
                && Objects.equals(getType(), other.getType());
    }
}