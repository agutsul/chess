package com.agutsul.chess.player.event;

import com.agutsul.chess.player.Player;

public class PlayerActionEvent
        extends AbstractResponseEvent {

    private final String source;
    private final String target;

    public PlayerActionEvent(Player player, String source, String target) {
        super(player);
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }
    public String getTarget() {
        return target;
    }
}