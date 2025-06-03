package com.agutsul.chess.game.observer;

import static java.time.LocalDateTime.now;
import static org.apache.commons.io.IOUtils.close;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import org.slf4j.Logger;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.event.GameOverEvent;

public final class GameOverObserver
        implements Observer {

    private static final Logger LOGGER = getLogger(GameOverObserver.class);

    @Override
    public void observe(Event event) {
        if (event instanceof GameOverEvent) {
            process((GameOverEvent) event);
        }
    }

    private void process(GameOverEvent event) {
        var game = event.getGame();
        try {
            ((Observable) game.getBoard()).notifyObservers(event);
        } finally {
            try {
                 close(game.getContext());
            } catch (IOException e) {
                LOGGER.error("Closing game context failed", e);
            } finally {
                ((AbstractGame) game).setFinishedAt(now());
            }
        }
    }
}