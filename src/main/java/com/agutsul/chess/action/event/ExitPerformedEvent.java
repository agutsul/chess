package com.agutsul.chess.action.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

public class ExitPerformedEvent
        implements Event {

    private final Player player;

    public ExitPerformedEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}