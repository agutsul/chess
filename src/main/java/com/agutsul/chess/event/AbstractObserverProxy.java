package com.agutsul.chess.event;

public abstract class AbstractObserverProxy
        implements Observer {

    private final Observer observer;

    protected AbstractObserverProxy(Observer observer) {
        this.observer = observer;
    }

    @Override
    public final void observe(Event event) {
        this.observer.observe(event);
    }
}