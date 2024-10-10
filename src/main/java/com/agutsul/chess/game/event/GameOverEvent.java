package com.agutsul.chess.game.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.Game;

public class GameOverEvent
        implements Event {

    private final Game game;

    public GameOverEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}