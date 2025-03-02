package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.player.Player;

public class ActionExecutionEvent
        extends AbstractProcessingActionEvent {

    private final Player player;

    public ActionExecutionEvent(Player player, Action<?> action) {
        super(action);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}