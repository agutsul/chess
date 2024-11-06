package com.agutsul.chess.player.event;

import com.agutsul.chess.player.Player;

public class PlayerDrawActionEvent
        extends AbstractResponseEvent {

    public PlayerDrawActionEvent(Player player) {
        super(player);
    }
}