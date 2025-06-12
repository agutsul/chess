package com.agutsul.chess.event;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public final class CompositeEventObserver
        implements Observer {

    // [event type class, observer]
    private final MultiValuedMap<Type,AbstractEventObserver<?>> observers = new ArrayListValuedHashMap<>();

    public CompositeEventObserver(AbstractEventObserver<?> observer,
                                  AbstractEventObserver<?>... observers) {

        Stream.of(List.of(observer), List.of(observers))
            .flatMap(Collection::stream)
            .forEach(obs -> this.observers.put(obs.getEventType(), obs));
    }

    @Override
    public void observe(Event event) {
        Stream.ofNullable(this.observers.get(event.getClass()))
            .flatMap(Collection::stream)
            .forEach(observer -> observer.observe(event));
    }
}