package com.agutsul.chess.game;

import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import com.agutsul.chess.timeout.Timeout;

public final class GameContext implements Closeable {

    private String event;
    private String site;
    private String round;

    private ForkJoinPool forkJoinPool;

    // milliseconds
    private Long actionTimeout;
    private Long gameTimeout;
    private Long extraActionTime;
    private Integer totalActions;

    private Optional<Timeout> timeout;

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
        return timeout;
    }
    public void setTimeout(Optional<Timeout> timeout) {
        this.timeout = timeout;
    }

/**/
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
    public Long getExtraActionTime() {
        return extraActionTime;
    }
    public void setExtraActionTime(Long extraActionTime) {
        this.extraActionTime = extraActionTime;
    }
    public Integer getTotalActions() {
        return totalActions;
    }
    public void setTotalActions(Integer totalActions) {
        this.totalActions = totalActions;
    }
/**/
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