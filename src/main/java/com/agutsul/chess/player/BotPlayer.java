package com.agutsul.chess.player;

import java.util.UUID;

import com.agutsul.chess.Color;

// Under development
public class BotPlayer
        extends AbstractPlayer {

    public BotPlayer(UUID uuid, Color color) {
        super(String.valueOf(uuid), color);
    }
}