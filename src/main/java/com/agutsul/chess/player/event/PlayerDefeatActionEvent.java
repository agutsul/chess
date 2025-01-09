package com.agutsul.chess.player.event;

import com.agutsul.chess.player.Player;

public class PlayerDefeatActionEvent
        extends AbstractResponseEvent {

    public PlayerDefeatActionEvent(Player player) {
        super(player);
    }
}