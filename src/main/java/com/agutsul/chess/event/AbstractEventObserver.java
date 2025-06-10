package com.agutsul.chess.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public abstract class AbstractEventObserver<EVENT extends Event>
        implements Observer {

    protected final Type eventType;

    protected AbstractEventObserver() {
        this.eventType =
                ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void observe(Event event) {
        if (Objects.equals(this.eventType, event.getClass())) {
            process((EVENT) event);
        }
    }

    protected abstract void process(EVENT event);
}