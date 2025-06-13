package com.agutsul.chess.player.observer;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.Game;

abstract class AbstractPlayerObserver
        implements Observer {

    protected final Game game;
    private final Observer observer;

    AbstractPlayerObserver(Game game) {
        this.game = game;
        this.observer = createObserver();
    }

    @Override
    public void observe(Event event) {
        this.observer.observe(event);
    }

    protected abstract Observer createObserver();

    protected final void notifyBoardEvent(Event event) {
        var board = this.game.getBoard();
        ((Observable) board).notifyObservers(event);
    }

    protected final void notifyGameEvent(Event event) {
        ((Observable) this.game).notifyObservers(event);
    }
}