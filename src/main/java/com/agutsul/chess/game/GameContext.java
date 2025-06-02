package com.agutsul.chess.game;

import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public final class GameContext implements Closeable {

    private ForkJoinPool forkJoinPool;

    // milliseconds
    private Long actionTimeout;
    private Long gameTimeout;

    public GameContext() {}

    public GameContext(ForkJoinPool forkJoinPool) {
        this(forkJoinPool, null, null);
    }

    public GameContext(ForkJoinPool forkJoinPool, Long actionTimeoutMillis, Long gameTimeoutMillis) {
        this.forkJoinPool  = forkJoinPool;
        this.actionTimeout = actionTimeoutMillis;
        this.gameTimeout   = gameTimeoutMillis;
    }

    public Long getActionTimeout() {
        return actionTimeout;
    }
    public void setActionTimeout(Long actionTimeout) {
        this.actionTimeout = actionTimeout;
    }
    public Long getGameTimeout() {
        return gameTimeout;
    }
    public void setGameTimeout(Long gameTimeout) {
        this.gameTimeout = gameTimeout;
    }
    public ForkJoinPool getForkJoinPool() {
        return forkJoinPool;
    }
    public void setForkJoinPool(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
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