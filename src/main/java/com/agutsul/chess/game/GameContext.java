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
import com.agutsul.chess.timeout.MixedTimeout;
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
                .filter(timeout -> timeout.isAnyType(Type.SANDCLOCK, Type.ACTIONS_PER_PERIOD))
                .map(timeout -> (ActionTimeout) timeout)
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
                .filter(timeout -> timeout.isAnyType(Type.GENERIC, Type.ACTIONS_PER_PERIOD))
                .map(timeout -> (GameTimeout) timeout)
                .map(GameTimeout::getGameDuration)
                .map(Duration::toMillis)
                .findFirst();
    }

    public Optional<Long> getExtraActionTime() {
        return Stream.ofNullable(getTimeout())
                .flatMap(Optional::stream)
                .filter(timeout -> timeout.isType(Type.INCREMENTAL))
                .map(timeout -> (IncrementalTimeout) timeout)
                .map(IncrementalTimeout::getExtraDuration)
                .map(Duration::toMillis)
                .findFirst();
    }

    public Optional<Integer> getTotalActions() {
        return Stream.ofNullable(getTimeout())
                .flatMap(Optional::stream)
                .filter(timeout -> timeout.isType(Type.ACTIONS_PER_PERIOD))
                .map(timeout -> (MixedTimeout) timeout)
                .map(MixedTimeout::getActionsCounter)
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
}