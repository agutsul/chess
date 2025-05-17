package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.event.GameTerminationEvent;
import com.agutsul.chess.player.Player;

public class ActionTerminatedEvent
        implements Event, GameTerminationEvent {

    private final Player player;
    private final Type type;

    public ActionTerminatedEvent(Player player, Type type) {
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