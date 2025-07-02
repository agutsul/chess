package com.agutsul.chess.timeout;

import static com.agutsul.chess.timeout.TimeoutFactory.createActionTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;

import java.time.Duration;
import java.util.Optional;

final class ActionsGameTimeoutImpl<GT extends Timeout & GameTimeout, AT extends Timeout & ActionTimeout>
        extends AbstractTimeout
        implements ActionTimeout, GameTimeout, ActionsGameTimeout {

    private final GT gameTimeout;
    private final AT actionTimeout;
    private final int actionCounter;

    ActionsGameTimeoutImpl(long durationMillis, int actionCounter) {
        this(
                createGameTimeout(durationMillis),
                createActionTimeout(calculateActionTimeout(durationMillis, actionCounter)),
                actionCounter
        );
    }

    private ActionsGameTimeoutImpl(GT gameTimeout, AT actionTimeout, int actionCounter) {
        super(Timeout.Type.ACTIONS_PER_PERIOD);

        this.gameTimeout = gameTimeout;
        this.actionTimeout = actionTimeout;
        this.actionCounter = actionCounter;
    }

    @Override
    public boolean isType(Type type) {
        return this.gameTimeout.isType(type)
                || this.actionTimeout.isType(type)
                || super.isType(type);
    }

    @Override
    public Optional<Duration> getDuration() {
        return Optional.of(getGameDuration());
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

    private static long calculateActionTimeout(long durationMillis, int actionCounter) {
        var halfActionMillis = durationMillis / 2;
        return halfActionMillis / actionCounter;
    }
}