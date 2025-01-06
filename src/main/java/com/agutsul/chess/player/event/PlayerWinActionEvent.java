package com.agutsul.chess.player.event;

import com.agutsul.chess.player.Player;

public class PlayerWinActionEvent
        extends AbstractResponseEvent {

    public PlayerWinActionEvent(Player player) {
        super(player);
    }
}