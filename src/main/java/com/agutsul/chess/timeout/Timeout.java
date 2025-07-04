package com.agutsul.chess.timeout;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

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

    default boolean isType(Type type) {
        return Objects.equals(getType(), type);
    }

    default boolean isAnyType(Type type, Type... types) {
        return isType(type) || Stream.of(types).anyMatch(this::isType);
    }

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