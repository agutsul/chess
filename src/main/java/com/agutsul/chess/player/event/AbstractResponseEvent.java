package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

public abstract class AbstractResponseEvent
        implements Event {

    private final Player player;

    AbstractResponseEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}