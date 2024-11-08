package com.agutsul.chess.action.memento;

import java.time.LocalDateTime;

import com.agutsul.chess.action.Action;

abstract class AbstractActionMemento<SOURCE,TARGET>
        implements ActionMemento<SOURCE,TARGET> {

    private final LocalDateTime createdAt;
    private final Action.Type actionType;
    private final SOURCE source;
    private final TARGET target;

    AbstractActionMemento(LocalDateTime createdAt,
                          Action.Type actionType,
                          SOURCE source,
                          TARGET target) {
        this.createdAt = createdAt;
        this.actionType = actionType;
        this.source = source;
        this.target = target;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public Action.Type getActionType() {
        return actionType;
    }

    @Override
    public SOURCE getSource() {
        return source;
    }

    @Override
    public TARGET getTarget() {
        return target;
    }
}