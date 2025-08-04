package com.agutsul.chess.game;

import static java.lang.Thread.currentThread;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.timeout.ActionTimeout;
import com.agutsul.chess.timeout.GameTimeout;
import com.agutsul.chess.timeout.IncrementalTimeout;
import com.agutsul.chess.timeout.MixedTimeout;
import com.agutsul.chess.timeout.Timeout;
import com.agutsul.chess.timeout.Timeout.Type;

public class GameContext implements Closeable {

    private static final Logger LOGGER = getLogger(GameContext.class);

    private String event;
    private String site;
    private String round;

    private ForkJoinPool forkJoinPool;
    private Timeout timeout;

    public GameContext() {}

    public GameContext(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }

    public GameContext(GameContext context) {
        this.event = context.getEvent();
        this.site  = context.getSite();
        this.round = context.getRound();
        this.forkJoinPool = context.getForkJoinPool();
        this.timeout = Stream.of(context.getTimeout())
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(null);
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
        return Stream.of(getTimeout())
                .flatMap(Optional::stream)
                .map(timeout -> timeout.isType(Type.INCREMENTAL)
                            ? ((IncrementalTimeout<?>) timeout).getTimeout()
                            : timeout
                )
                .filter(timeout -> timeout.isAnyType(Type.SANDCLOCK, Type.ACTIONS_PER_PERIOD))
                .map(timeout -> (ActionTimeout) timeout)
                .map(ActionTimeout::getActionDuration)
                .map(Duration::toMillis)
                .filter(timeout -> timeout > 0)
                .findFirst();
    }

    public Optional<Long> getGameTimeout() {
        return Stream.of(getTimeout())
                .flatMap(Optional::stream)
                .map(timeout -> timeout.isType(Type.INCREMENTAL)
                            ? ((IncrementalTimeout<?>) timeout).getTimeout()
                            : timeout
                )
                .filter(timeout -> timeout.isAnyType(Type.GENERIC, Type.ACTIONS_PER_PERIOD))
                .map(timeout -> (GameTimeout) timeout)
                .map(GameTimeout::getGameDuration)
                .map(Duration::toMillis)
                .findFirst();
    }

    public Optional<Long> getExtraActionTimeout() {
        return Stream.of(getTimeout())
                .flatMap(Optional::stream)
                .filter(timeout -> timeout.isType(Type.INCREMENTAL))
                .map(timeout -> (IncrementalTimeout<?>) timeout)
                .map(IncrementalTimeout::getExtraDuration)
                .map(Duration::toMillis)
                .filter(timeout -> timeout > 0)
                .findFirst();
    }

    public Optional<Integer> getExpectedActions() {
        return Stream.of(getTimeout())
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

                if (!forkJoinPool.awaitTermination(1, MILLISECONDS)) {
                    LOGGER.error("Game context executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            forkJoinPool.shutdownNow();
            currentThread().interrupt();
        }
    }
}