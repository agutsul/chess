package com.agutsul.chess.timeout;

import static com.agutsul.chess.timeout.TimeoutFactory.createActionTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;

import java.time.Duration;
import java.util.Optional;

final class MixedTimeoutImpl<GT extends Timeout & GameTimeout, AT extends Timeout & ActionTimeout>
        extends AbstractBaseTimeout
        implements ActionTimeout, GameTimeout, MixedTimeout {

    private final GT  gameTimeout;
    private final AT  actionTimeout;
    private final int actionCounter;

    MixedTimeoutImpl(long durationMillis, int actionCounter) {
        this(
                createGameTimeout(durationMillis),
                createActionTimeout(calculateActionTimeout(durationMillis, actionCounter)),
                actionCounter
        );
    }

    private MixedTimeoutImpl(GT gameTimeout, AT actionTimeout, int actionCounter) {
        super(Type.ACTIONS_PER_PERIOD);

        this.gameTimeout   = gameTimeout;
        this.actionTimeout = actionTimeout;
        this.actionCounter = actionCounter;
    }

    @Override
    public Duration getGameDuration() {
        return this.gameTimeout.getGameDuration();
    }

    @Override
    public Duration getActionDuration() {
        return this.actionTimeout.getActionDuration();
    }

    @Override
    public int getActionsCounter() {
        return this.actionCounter;
    }

    @Override
    public boolean isType(Type type) {
        return this.gameTimeout.isType(type)
                || this.actionTimeout.isType(type)
                || super.isType(type);
    }

    @Override
    public boolean isAnyType(Type type, Type... types) {
        return this.gameTimeout.isAnyType(type, types)
                || this.actionTimeout.isAnyType(type, types)
                || super.isAnyType(type, types);
    }

    @Override
    public Optional<Duration> getDuration() {
        return Optional.of(getGameDuration());
    }

    @Override
    public String toString() {
        return String.format("%d/%d",
                getActionsCounter(), getGameDuration().toSeconds()
        );
    }

    private static long calculateActionTimeout(long durationMillis, int actionCounter) {
        if (actionCounter == 0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid actions counter: %d",
                    actionCounter
            ));
        }

        var halfActionMillis = durationMillis / 2;
        return halfActionMillis / actionCounter;
    }
}