package com.agutsul.chess.player.event;

import com.agutsul.chess.player.Player;

public class RequestPlayerActionEvent
        extends AbstractRequestEvent {

    private final Player player;

    public RequestPlayerActionEvent(Player player) {
        super(player.getColor());
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}