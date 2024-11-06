package com.agutsul.chess.action.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

public class DrawPerformedEvent
        implements Event {

    private final Player player;

    public DrawPerformedEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}