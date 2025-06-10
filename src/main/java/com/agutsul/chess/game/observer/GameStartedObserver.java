package com.agutsul.chess.game.observer;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.event.GameStartedEvent;

public final class GameStartedObserver
        extends AbstractEventObserver<GameStartedEvent> {

    @Override
    protected void process(GameStartedEvent event) {
        ((AbstractGame) event.getGame()).setStartedAt(now());
    }
}