package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Game;

public class GameStartedEvent
        extends AbstractGameEvent {

    public GameStartedEvent(Game game) {
        super(game);
    }
}