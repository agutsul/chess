package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Game;

public class GameTimeoutTerminationEvent
        extends AbstractGameEvent
        implements GameTerminationEvent {

    public GameTimeoutTerminationEvent(Game game) {
        super(game);
    }

    @Override
    public Type getType() {
        return Type.TIMEOUT;
    }
}