package com.agutsul.chess.game;

import java.time.LocalDateTime;
import java.util.Optional;

import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.player.Player;

public interface Game extends Runnable {

    Player getWhitePlayer();
    Player getBlackPlayer();

    LocalDateTime getStartedAt();
    LocalDateTime getFinishedAt();

    Journal<Memento> getJournal();
    Optional<Player> getWinner();
}