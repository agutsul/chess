package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class RequestPlayerActionEvent
        implements Event {

    @SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
    private final Player player;

    public RequestPlayerActionEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}