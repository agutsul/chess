package com.agutsul.chess.game;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import com.agutsul.chess.game.state.BlackWinGameState;
import com.agutsul.chess.game.state.DefaultGameState;
import com.agutsul.chess.game.state.DrawnGameState;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.game.state.WhiteWinGameState;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.player.Player;

public class GameContext {

    private final Player whitePlayer;
    private final Player blackPlayer;
    private final Journal<Memento> journal;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private Optional<Player> winner;

    GameContext(Player whitePlayer,
                Player blackPlayer,
                Journal<Memento> journal) {

        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.journal = journal;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public Journal<Memento> getJournal() {
        return journal;
    }

    public Optional<Player> getWinner() {
        return winner;
    }

    public GameState getGameState() {
        if (getFinishedAt() == null) {
            return new DefaultGameState();
        }

        var winner = getWinner();
        if (winner.isEmpty()) {
            return new DrawnGameState();
        }

        if (Objects.equals(whitePlayer, winner.get())) {
            return new WhiteWinGameState();
        }

        return new BlackWinGameState();
    }

    public void setWinner(Optional<Player> winner) {
        this.winner = winner;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }


}
