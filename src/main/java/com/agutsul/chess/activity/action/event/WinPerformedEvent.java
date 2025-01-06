package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

public class WinPerformedEvent
        implements Event {

    private final Player player;

    public WinPerformedEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}