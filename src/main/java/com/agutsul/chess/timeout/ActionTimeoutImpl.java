package com.agutsul.chess.timeout;

import java.time.Duration;

final class ActionTimeoutImpl
        extends AbstractTimeout
        implements ActionTimeout {

    ActionTimeoutImpl(long millis) {
        super(Timeout.Type.SANDCLOCK, millis);
    }

    @Override
    public Duration getActionDuration() {
        return super.getDuration().get();
    }

    @Override
    public String toString() {
        return String.format("*%d", getActionDuration().toSeconds());
    }
}