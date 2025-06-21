package com.agutsul.chess.player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.player.state.ActivePlayerState;
import com.agutsul.chess.player.state.LockedPlayerState;
import com.agutsul.chess.player.state.PlayerState;

public abstract class AbstractPlayer
        implements Player, Observable {

    private static final PlayerState ACTIVE_STATE = new ActivePlayerState();
    private static final PlayerState LOCKED_STATE = new LockedPlayerState();

    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    private final String name;
    private final Color color;

    private PlayerState state;

    public AbstractPlayer(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public final void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public final void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public final void notifyObservers(Event event) {
        for (var observer : this.observers) {
            observer.observe(event);
        }
    }

    @Override
    public PlayerState getState() {
        return state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void enable() {
        setState(ACTIVE_STATE);
    }

    @Override
    public void disable() {
        setState(LOCKED_STATE);
    }

    void setState(PlayerState state) {
        this.state = state;
    }
}