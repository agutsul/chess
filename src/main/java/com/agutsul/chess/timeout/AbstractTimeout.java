package com.agutsul.chess.timeout;

import java.time.Duration;
import java.util.Optional;

abstract class AbstractTimeout implements Timeout {

    private final Timeout.Type type;
    private final Duration duration;

    AbstractTimeout(Timeout.Type type) {
        this(type, null);
    }

    AbstractTimeout(Timeout.Type type, long milliseconds) {
        this(type, Duration.ofMillis(milliseconds));
    }

    AbstractTimeout(Timeout.Type type, Duration duration) {
        this.type = type;
        this.duration = duration;
    }

    @Override
    public final Type getType() {
        return type;
    }

    @Override
    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }
}