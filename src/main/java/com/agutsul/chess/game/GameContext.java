package com.agutsul.chess.game;

import java.util.concurrent.ForkJoinPool;

public class GameContext {

    private ForkJoinPool forkJoinPool;

    // milliseconds
    private Long actionTimeout;

    public GameContext() {}

    public GameContext(ForkJoinPool forkJoinPool, Long actionTimeoutMillis) {
        this.forkJoinPool  = forkJoinPool;
        this.actionTimeout = actionTimeoutMillis;
    }

    public Long getActionTimeout() {
        return actionTimeout;
    }
    public void setActionTimeout(Long actionTimeout) {
        this.actionTimeout = actionTimeout;
    }
    public ForkJoinPool getForkJoinPool() {
        return forkJoinPool;
    }
    public void setForkJoinPool(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }
}