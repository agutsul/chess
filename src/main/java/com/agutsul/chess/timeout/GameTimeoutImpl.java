package com.agutsul.chess.timeout;

import java.time.Duration;

final class GameTimeoutImpl
        extends AbstractTimeout
        implements GameTimeout {

    GameTimeoutImpl(long millis) {
        super(Timeout.Type.GENERIC, millis);
    }

    @Override
    public Duration getGameDuration() {
        return super.getDuration().get();
    }
}