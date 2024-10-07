package com.agutsul.chess.player;

import com.agutsul.chess.Color;
import com.agutsul.chess.player.state.PlayerState;

public interface Player {
    String getName();
    Color getColor();

    PlayerState getState();
    void setState(PlayerState state);

    void play();
}