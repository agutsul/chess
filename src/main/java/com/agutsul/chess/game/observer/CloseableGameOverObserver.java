package com.agutsul.chess.game.observer;

import static org.apache.commons.io.IOUtils.close;
import static org.apache.commons.lang3.ClassUtils.getName;
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
        var source = getName(this.closeable, Closeable.class.getName());
        LOGGER.info("Closing '{}' started...", source);

        try {
            close(this.closeable);
            LOGGER.info("Closing '{}' finished", source);
        } catch (IOException e) {
            LOGGER.error(String.format("Closing '%s' failed", source), e);
        }
    }
}