package com.agutsul.chess.game.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.Game;

public abstract class AbstractGameEvent
        implements Event {

    private final Game game;

    protected AbstractGameEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}