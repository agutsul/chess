package com.agutsul.chess.timeout;

final class UnknownTimeoutImpl
        extends AbstractTimeout
        implements UnknownTimeout {

    UnknownTimeoutImpl() {
        super(Timeout.Type.UNKNOWN);
    }
}