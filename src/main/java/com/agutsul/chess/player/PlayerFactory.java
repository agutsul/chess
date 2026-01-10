package com.agutsul.chess.player;

import static java.util.UUID.randomUUID;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;

public abstract class PlayerFactory {

    public static <PLAYER extends Player & Observable> PLAYER playerOf(Color color) {
        return playerOf(color, String.format("%s-%s", color, randomUUID()));
    }

    @SuppressWarnings("unchecked")
    public static <PLAYER extends Player & Observable> PLAYER playerOf(Color color, String name) {
        return (PLAYER) new PlayerImpl(name, color);
    }
}