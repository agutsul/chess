package com.agutsul.chess.game;

import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import com.agutsul.chess.timeout.ActionTimeout;
import com.agutsul.chess.timeout.ActionsGameTimeout;
import com.agutsul.chess.timeout.GameTimeout;
import com.agutsul.chess.timeout.IncrementalTimeout;
import com.agutsul.chess.timeout.Timeout;
import com.agutsul.chess.timeout.Timeout.Type;

public final class GameContext implements Closeable {

    private String event;
    private String site;
    private String round;

    private ForkJoinPool forkJoinPool;
    private Timeout timeout;

    public GameContext() {}

    public GameContext(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }

    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }
    public String getRound() {
        return round;
    }
    public void setRound(String round) {
        this.round = round;
    }
    public ForkJoinPool getForkJoinPool() {
        return forkJoinPool;
    }
    public void setForkJoinPool(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }
    public Optional<Timeout> getTimeout() {
        return Optional.ofNullable(timeout);
    }
    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    public Optional<Long> getActionTimeout() {
        return Stream.ofNullable(getTimeout())
                .flatMap(Optional::stream)
                .map(timeout -> timeout.isType(Type.INCREMENTAL)
                            ? ((IncrementalTimeout) timeout).getTimeout()
                            : timeout
                )
                .map(GameContext::getActionTimeout)
                .flatMap(Optional::stream)
                .map(ActionTimeout::getActionDuration)
                .map(Duration::toMillis)
                .findFirst();
    }

    public Optional<Long> getGameTimeout() {
        return Stream.ofNullable(getTimeout())
                .flatMap(Optional::stream)
                .map(timeout -> timeout.isType(Type.INCREMENTAL)
                            ? ((IncrementalTimeout) timeout).getTimeout()
                            : timeout
                )
                .map(GameContext::getGameTimeout)
                .flatMap(Optional::stream)
                .map(GameTimeout::getGameDuration)
                .map(Duration::toMillis)
                .findFirst();
    }

    public Optional<Long> getExtraActionTime() {
        return Stream.ofNullable(getTimeout())
                .flatMap(Optional::stream)
                .map(GameContext::getIncrementalTimeout)
                .flatMap(Optional::stream)
                .map(IncrementalTimeout::getExtraDuration)
                .map(Duration::toMillis)
                .findFirst();
    }

    public Optional<Integer> getTotalActions() {
        return Stream.ofNullable(getTimeout())
                .flatMap(Optional::stream)
                .map(GameContext::getActionsGameTimeout)
                .flatMap(Optional::stream)
                .map(ActionsGameTimeout::getActionsCounter)
                .findFirst();
    }

    @Override
    public void close() throws IOException {
        if (!isNull(getForkJoinPool())) {
            close(getForkJoinPool());
        }
    }

    private static void close(ForkJoinPool forkJoinPool) {
        try {
            forkJoinPool.shutdown();
            if (!forkJoinPool.awaitTermination(1, MILLISECONDS)) {
                forkJoinPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            forkJoinPool.shutdownNow();
        }
    }

    private static Optional<GameTimeout> getGameTimeout(Timeout timeout) {
        return Optional.ofNullable(timeout.isAnyType(Type.GENERIC, Type.ACTIONS_PER_PERIOD)
            ? (GameTimeout) timeout
            : null
        );
    }

    private static Optional<ActionTimeout> getActionTimeout(Timeout timeout) {
        return Optional.ofNullable(timeout.isAnyType(Type.SANDCLOCK, Type.ACTIONS_PER_PERIOD)
            ? (ActionTimeout) timeout
            : null
        );
    }

    private static Optional<IncrementalTimeout> getIncrementalTimeout(Timeout timeout) {
        return Optional.ofNullable(timeout.isType(Type.INCREMENTAL)
            ? (IncrementalTimeout) timeout
            : null
        );
    }

    private static Optional<ActionsGameTimeout> getActionsGameTimeout(Timeout timeout) {
        return Optional.ofNullable(timeout.isType(Type.ACTIONS_PER_PERIOD)
            ? (ActionsGameTimeout) timeout
            : null
        );
    }
}