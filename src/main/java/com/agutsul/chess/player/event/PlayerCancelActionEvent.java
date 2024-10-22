package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

public class PlayerCancelActionEvent
        implements Event {

    private final Player player;

    public PlayerCancelActionEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}