package com.agutsul.chess.player.event;

import com.agutsul.chess.player.Player;

public class PlayerCancelActionEvent
        extends AbstractResponseEvent {

    public PlayerCancelActionEvent(Player player) {
        super(player);
    }
}