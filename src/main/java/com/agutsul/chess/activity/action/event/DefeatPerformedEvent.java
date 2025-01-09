package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

public class DefeatPerformedEvent
        implements Event {

    private final Player player;

    public DefeatPerformedEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}