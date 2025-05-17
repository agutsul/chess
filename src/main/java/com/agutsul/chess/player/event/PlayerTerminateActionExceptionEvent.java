package com.agutsul.chess.player.event;

import com.agutsul.chess.game.event.GameTerminationEvent;
import com.agutsul.chess.player.Player;

public class PlayerTerminateActionExceptionEvent
        extends AbstractExceptionEvent
        implements GameTerminationEvent {

    private final Player player;
    private final Type type;

    public PlayerTerminateActionExceptionEvent(Player player, String message, Type type) {
        super(message);

        this.player = player;
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Type getType() {
        return type;
    }
}