package com.agutsul.chess.event;

import static org.apache.commons.lang3.ClassUtils.getAllSuperclasses;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractEventObserver<EVENT extends Event>
        implements Observer {

    private final Type eventType;

    protected AbstractEventObserver() {
        this.eventType = resolveEventType(getClass());
                //((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void observe(Event event) {
        if (Objects.equals(this.eventType, event.getClass())) {
            process((EVENT) event);
        }
    }

    public Type getEventType() {
        return this.eventType;
    }

    protected abstract void process(EVENT event);

    private static Type resolveEventType(Class<?> clazz) {
        return Stream.of(List.of(clazz), getAllSuperclasses(clazz))
                .flatMap(Collection::stream)
                .filter(cls -> Objects.equals(AbstractEventObserver.class, cls.getSuperclass()))
                .map(cls -> ((ParameterizedType) cls.getGenericSuperclass()).getActualTypeArguments()[0])
                .findFirst()
                .orElse(null);
/*
        var classes =  getAllSuperclasses(clazz);
        for (var cls : classes) {
            if (Objects.equals(AbstractEventObserver.class, cls.getSuperclass())) {
                var genericType = cls.getGenericSuperclass();
                return ((ParameterizedType) genericType).getActualTypeArguments()[0];
            }
        }

        return null;
*/
    }
}