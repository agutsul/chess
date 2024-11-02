package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Game;

public class GameOverEvent
        extends AbstractGameEvent {

    public GameOverEvent(Game game) {
        super(game);
    }
}