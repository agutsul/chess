package com.agutsul.chess.timeout;

import java.time.Duration;
import java.util.Optional;

final class IncrementalTimeoutImpl<TIMEOUT extends BaseTimeout>
        extends AbstractTimeout
        implements IncrementalTimeout<TIMEOUT> {

    private final TIMEOUT  timeout;
    private final Duration extraDuration;

    IncrementalTimeoutImpl(TIMEOUT timeout, long extraMillis) {
        super(Type.INCREMENTAL);
        this.timeout = timeout;
        this.extraDuration = Duration.ofMillis(extraMillis);
    }

    @Override
    public Duration getExtraDuration() {
        return extraDuration;
    }

    @Override
    public TIMEOUT getTimeout() {
        return timeout;
    }

    @Override
    public boolean isType(Type type) {
        return timeout.isType(type)
                || super.isType(type);
    }

    @Override
    public boolean isAnyType(Type type, Type... types) {
        return timeout.isAnyType(type, types)
                || super.isAnyType(type, types);
    }

    @Override
    public Optional<Duration> getDuration() {
        return timeout.getDuration();
    }

    @Override
    public String toString() {
        return String.format("%s+%d",
                getTimeout(), getExtraDuration().toSeconds()
        );
    }
}