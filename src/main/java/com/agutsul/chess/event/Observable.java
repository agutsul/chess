package com.agutsul.chess.event;

import java.util.Collection;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Event event);
    Collection<Observer> getObservers();
}