package com.agutsul.chess.game.observer;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;

import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.game.event.GameOverEvent;

public final class CloseableGameOverObserver
        extends AbstractEventObserver<GameOverEvent> {

    private static final Logger LOGGER = getLogger(CloseableGameOverObserver.class);

    private final Closeable closeable;

    public CloseableGameOverObserver(Closeable closeable) {
        this.closeable = closeable;
    }

    @Override
    protected void process(GameOverEvent event) {
        var source = this.closeable.getClass().getSimpleName();
        LOGGER.debug("Closing '{}' started...", source);

        try {
            this.closeable.close();
            LOGGER.debug("Closing '{}' finished", source);
        } catch (IOException e) {
            LOGGER.error(String.format("Closing '%s' failed", source), e);
        }
    }
}