package com.agutsul.chess.player.event;

import com.agutsul.chess.game.event.GameTerminationEvent;
import com.agutsul.chess.player.Player;

public final class PlayerTerminateActionEvent
        extends AbstractResponseEvent
        implements GameTerminationEvent {

    private final Type type;

    public PlayerTerminateActionEvent(Player player, Type type) {
        super(player);
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }
}