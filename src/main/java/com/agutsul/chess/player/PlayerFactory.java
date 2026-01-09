package com.agutsul.chess.player;

import static java.util.UUID.randomUUID;

import com.agutsul.chess.color.Color;

public enum PlayerFactory {
    INSTANCE;

    public Player create(Color color) {
        return new UserPlayer(String.format("%s-%s", color, randomUUID()), color);
    }

    public Player create(Color color, String name) {
        return new UserPlayer(name, color);
    }

    public static Player playerOf(Color color) {
        return INSTANCE.create(color);
    }

    public static Player playerOf(Color color, String name) {
        return INSTANCE.create(color, name);
    }
}