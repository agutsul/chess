package com.agutsul.chess.game;

import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.game.state.BlackWinGameState;
import com.agutsul.chess.game.state.DefaultGameState;
import com.agutsul.chess.game.state.DrawnGameState;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.game.state.WhiteWinGameState;
import com.agutsul.chess.player.Player;

abstract class AbstractGame
        implements Game {

    protected final Logger logger;

    protected final Player whitePlayer;
    protected final Player blackPlayer;

    protected String event;
    protected String site;
    protected String round;

    protected LocalDateTime startedAt;
    protected LocalDateTime finishedAt;

    AbstractGame(Logger logger, Player whitePlayer, Player blackPlayer) {
        this.logger = logger;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    @Override
    public final String getEvent() {
        return event;
    }

    @Override
    public final String getSite() {
        return site;
    }

    @Override
    public final String getRound() {
        return round;
    }

    @Override
    public final Player getWhitePlayer() {
        return whitePlayer;
    }

    @Override
    public final Player getBlackPlayer() {
        return blackPlayer;
    }

    @Override
    public final LocalDateTime getStartedAt() {
        return startedAt;
    }

    @Override
    public final LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    @Override
    public final GameState getState() {
        if (Objects.isNull(getFinishedAt())) {
            return new DefaultGameState();
        }

        var winner = getWinner();
        if (winner.isEmpty()) {
            return new DrawnGameState();
        }

        var state = Objects.equals(getWhitePlayer(), winner.get())
                ? new WhiteWinGameState()
                : new BlackWinGameState();

        return state;
    }
}