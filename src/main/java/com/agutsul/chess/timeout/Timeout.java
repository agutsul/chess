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
    boolean isAnyType(Type type, Type... additionalTypes);

    // utilities

    static boolean isGeneric(Timeout timeout) {
        return isGeneric(timeout.getType());
    }

    static boolean isGeneric(Timeout.Type type) {
        return Timeout.Type.GENERIC.equals(type);
    }

    static boolean isActionsPerPeriod(Timeout timeout) {
        return isActionsPerPeriod(timeout.getType());
    }

    static boolean isActionsPerPeriod(Timeout.Type type) {
        return Timeout.Type.ACTIONS_PER_PERIOD.equals(type);
    }

    static boolean isIncremental(Timeout timeout) {
        return isIncremental(timeout.getType());
    }

    static boolean isIncremental(Timeout.Type type) {
        return Timeout.Type.INCREMENTAL.equals(type);
    }

    static boolean isSandclock(Timeout timeout) {
        return isSandclock(timeout.getType());
    }

    static boolean isSandclock(Timeout.Type type) {
        return Timeout.Type.SANDCLOCK.equals(type);
    }

    static boolean isUnknown(Timeout timeout) {
        return isUnknown(timeout.getType());
    }

    static boolean isUnknown(Timeout.Type type) {
        return Timeout.Type.UNKNOWN.equals(type);
    }
}