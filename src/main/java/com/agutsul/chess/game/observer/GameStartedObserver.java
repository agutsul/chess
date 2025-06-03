package com.agutsul.chess.game.observer;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.event.GameStartedEvent;

public final class GameStartedObserver
        implements Observer {

    @Override
    public void observe(Event event) {
        if (event instanceof GameStartedEvent) {
            process((GameStartedEvent) event);
        }
    }

    private void process(GameStartedEvent event) {
        ((AbstractGame) event.getGame()).setStartedAt(now());
    }
}