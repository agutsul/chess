package com.agutsul.chess.timeout;

import java.time.Duration;
import java.util.Optional;

final class IncrementalTimeoutImpl
        extends AbstractTimeout
        implements IncrementalTimeout {

    private final Timeout timeout;
    private final Duration extraDuration;

    IncrementalTimeoutImpl(Timeout timeout, long extraMillis) {
        super(Timeout.Type.INCREMENTAL);
        this.timeout = timeout;
        this.extraDuration = Duration.ofMillis(extraMillis);
    }

    @Override
    public boolean isType(Type type) {
        return timeout.isType(type)
                || super.isType(type);
    }

    @Override
    public boolean isAnyType(Type type, Type... additionalTypes) {
        return timeout.isAnyType(type, additionalTypes)
                || super.isAnyType(type, additionalTypes);
    }

    @Override
    public Duration getExtraDuration() {
        return extraDuration;
    }

    @Override
    public Optional<Duration> getDuration() {
        return timeout.getDuration();
    }

    @Override
    public Timeout getTimeout() {
        return timeout;
    }
}