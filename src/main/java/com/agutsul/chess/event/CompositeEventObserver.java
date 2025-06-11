package com.agutsul.chess.event;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

public final class CompositeEventObserver
        implements Observer {

    // [event type class, observer]
    private final MultiMap observers = new MultiValueMap();

    public CompositeEventObserver(AbstractEventObserver<?> observer,
                                  AbstractEventObserver<?>... observers) {

        Stream.of(List.of(observer), List.of(observers))
            .flatMap(Collection::stream)
            .forEach(obs -> this.observers.put(obs.getEventType(), obs));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void observe(Event event) {
        Stream.ofNullable(this.observers.get(event.getClass()))
            .map(observers -> (Collection<AbstractEventObserver<?>>) observers)
            .flatMap(Collection::stream)
            .forEach(observer -> observer.observe(event));
    }
}