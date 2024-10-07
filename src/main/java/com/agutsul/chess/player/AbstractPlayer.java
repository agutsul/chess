package com.agutsul.chess.player;

import com.agutsul.chess.Color;
import com.agutsul.chess.player.state.PlayerState;

public abstract class AbstractPlayer implements Player {

    private final String name;
    private final Color color;

    private PlayerState state;

    public AbstractPlayer(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public void setState(PlayerState state) {
        this.state = state;
    }

    @Override
    public PlayerState getState() {
        return state;
    }

    @Override
    public void play() {
        state.play(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return getName();
    }
}