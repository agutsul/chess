package com.agutsul.chess.game.observer;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.AbstractGameProxy;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameStartedEvent;

public final class GameStartedObserver
        extends AbstractEventObserver<GameStartedEvent> {

    @Override
    protected void process(GameStartedEvent event) {
        register(event.getGame());
    }

    private static void register(Game game) {
        if (game instanceof AbstractGame) {
            ((AbstractGame) game).setStartedAt(now());
        } else {
            register(((AbstractGameProxy<?>) game).getOrigin());
        }
    }
}