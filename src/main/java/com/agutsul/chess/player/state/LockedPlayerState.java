package com.agutsul.chess.player.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.player.Player;

public final class LockedPlayerState
        extends AbstractPlayerState {

    private static final Logger LOGGER = getLogger(LockedPlayerState.class);

    public LockedPlayerState() {
        super(Type.LOCKED);
    }

    @Override
    public void play(Player player) {
        LOGGER.info("Request player '{}' action", player);
        // do nothing
    }
}