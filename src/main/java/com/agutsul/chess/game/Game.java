package com.agutsul.chess.game;

import java.util.Optional;

import com.agutsul.chess.player.Player;

public interface Game extends Runnable {
    Optional<Player> getWinner();
}