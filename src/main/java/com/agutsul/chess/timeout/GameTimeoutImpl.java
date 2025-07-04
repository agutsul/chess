package com.agutsul.chess.timeout;

import java.time.Duration;

final class GameTimeoutImpl
        extends AbstractTimeout
        implements GameTimeout {

    GameTimeoutImpl(long millis) {
        super(Type.GENERIC, millis);
    }

    @Override
    public Duration getGameDuration() {
        return super.getDuration().get();
    }

    @Override
    public String toString() {
        return String.format("%d", getGameDuration().toSeconds());
    }
}