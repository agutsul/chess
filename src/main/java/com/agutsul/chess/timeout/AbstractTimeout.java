package com.agutsul.chess.timeout;

import java.time.Duration;
import java.util.Optional;

abstract class AbstractTimeout implements Timeout {

    private final Type type;
    private final Duration duration;

    AbstractTimeout(Type type) {
        this(type, null);
    }

    AbstractTimeout(Type type, Duration duration) {
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