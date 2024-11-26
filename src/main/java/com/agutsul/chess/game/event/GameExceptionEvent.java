package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Game;

public class GameExceptionEvent
        extends AbstractGameEvent {

    private final Throwable throwable;

    public GameExceptionEvent(Game game, Throwable throwable) {
        super(game);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}