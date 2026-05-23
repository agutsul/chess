package com.agutsul.chess.game.observer;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.AbstractGameProxy;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameOverEvent;

public final class GameOverObserver
        extends AbstractEventObserver<GameOverEvent> {

    @Override
    protected void process(GameOverEvent event) {
        var game = event.getGame();
        try {
            ((Observable) game.getBoard()).notifyObservers(event);
        } finally {
            register(game);
        }
    }

    private static void register(Game game) {
        if (game instanceof AbstractGame) {
            ((AbstractGame) game).setFinishedAt(now());
        } else {
            register(((AbstractGameProxy<?>) game).getOrigin());
        }
    }
}