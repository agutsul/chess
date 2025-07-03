package com.agutsul.chess.timeout;

import java.time.Duration;
import java.util.Optional;

public interface Timeout {

    enum Type {
        GENERIC,
        ACTIONS_PER_PERIOD,
        INCREMENTAL,
        SANDCLOCK,
        UNKNOWN
    }

    Optional<Duration> getDuration();

    Type getType();

    boolean isType(Type type);
    boolean isAnyType(Type type, Type... types);

    // utilities

    static boolean isGeneric(Timeout timeout) {
        return isGeneric(timeout.getType());
    }

    static boolean isGeneric(Type type) {
        return Type.GENERIC.equals(type);
    }

    static boolean isActionsPerPeriod(Timeout timeout) {
        return isActionsPerPeriod(timeout.getType());
    }

    static boolean isActionsPerPeriod(Type type) {
        return Type.ACTIONS_PER_PERIOD.equals(type);
    }

    static boolean isIncremental(Timeout timeout) {
        return isIncremental(timeout.getType());
    }

    static boolean isIncremental(Type type) {
        return Type.INCREMENTAL.equals(type);
    }

    static boolean isSandclock(Timeout timeout) {
        return isSandclock(timeout.getType());
    }

    static boolean isSandclock(Type type) {
        return Type.SANDCLOCK.equals(type);
    }

    static boolean isUnknown(Timeout timeout) {
        return isUnknown(timeout.getType());
    }

    static boolean isUnknown(Type type) {
        return Type.UNKNOWN.equals(type);
    }
}