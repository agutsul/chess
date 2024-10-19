package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class PlayerCancelActionEvent
        implements Event {

    private final Player player;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public PlayerCancelActionEvent(Player player) {
        this.player = player;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Player getPlayer() {
        return player;
    }
}