package com.agutsul.chess.timeout;

import java.time.Duration;

abstract class AbstractBaseTimeout
        extends AbstractTimeout
        implements BaseTimeout {

    AbstractBaseTimeout(Type type) {
        super(type);
    }

    AbstractBaseTimeout(Type type, long milliseconds) {
        super(type, Duration.ofMillis(milliseconds));
    }
}