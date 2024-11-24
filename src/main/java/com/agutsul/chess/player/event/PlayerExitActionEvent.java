package com.agutsul.chess.player.event;

import com.agutsul.chess.player.Player;

public class PlayerExitActionEvent
        extends AbstractResponseEvent {

    public PlayerExitActionEvent(Player player) {
        super(player);
    }
}