package com.agutsul.chess.player;

import java.util.Optional;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.player.state.PlayerState;

public interface Player {
    String getName();
    Color getColor();
    PlayerState getState();

    Optional<Long> getExtraTimeout();
    void setExtraTimeout(Long timeout);

    void enable();
    void disable();
}