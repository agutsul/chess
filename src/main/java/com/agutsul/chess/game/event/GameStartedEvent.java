package com.agutsul.chess.game.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.Game;

public class GameStartedEvent implements Event {

    private final Game game;

    public GameStartedEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}