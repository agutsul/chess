package com.agutsul.chess.game;

import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public final class GameContext implements Closeable {

    private String event;
    private String site;
    private String round;

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