package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.player.Player;

public class ActionPerformedEvent
        extends AbstractProccessedActionEvent {

    private final Player player;
    private final ActionMemento<?,?> actionMemento;

    public ActionPerformedEvent(Player player, ActionMemento<?,?> actionMemento) {
        this.player = player;
        this.actionMemento = actionMemento;
    }

    public Player getPlayer() {
        return player;
    }

    public ActionMemento<?,?> getActionMemento() {
        return actionMemento;
    }
}