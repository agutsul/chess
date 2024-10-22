package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

public class PlayerActionEvent
        implements Event {

    private final Player player;
    private final String source;
    private final String target;

    public PlayerActionEvent(Player player, String source, String target) {
        this.player = player;
        this.source = source;
        this.target = target;
    }

    public Player getPlayer() {
        return player;
    }
    public String getSource() {
        return source;
    }
    public String getTarget() {
        return target;
    }
}