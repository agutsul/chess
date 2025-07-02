package com.agutsul.chess.timeout;

public abstract class TimeoutFactory {

    @SuppressWarnings("unchecked")
    public static final <T extends Timeout & GameTimeout> T createGameTimeout(long durationMillis) {
        return (T) new GameTimeoutImpl(durationMillis);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Timeout & ActionTimeout> T createActionTimeout(long durationMillis) {
        return (T) new ActionTimeoutImpl(durationMillis);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Timeout & ActionsGameTimeout> T createActionsGameTimeout(long durationMillis, int actionCounter) {
        return (T) new ActionsGameTimeoutImpl<>(durationMillis, actionCounter);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Timeout & IncrementalTimeout> T createIncrementalTimeout(Timeout timeout, long extraMillis) {
        return (T) new IncrementalTimeoutImpl(timeout, extraMillis);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Timeout & UnknownTimeout> T createUnknownTimeout() {
        return (T) new UnknownTimeoutImpl();
    }
}