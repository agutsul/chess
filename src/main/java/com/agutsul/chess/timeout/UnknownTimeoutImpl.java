package com.agutsul.chess.timeout;

final class UnknownTimeoutImpl
        extends AbstractTimeout
        implements UnknownTimeout {

    private static final String UNKNOWN_SYMBOL = "?";

    UnknownTimeoutImpl() {
        super(Type.UNKNOWN);
    }

    @Override
    public String toString() {
        return UNKNOWN_SYMBOL;
    }
}