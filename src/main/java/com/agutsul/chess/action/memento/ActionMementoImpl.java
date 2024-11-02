package com.agutsul.chess.action.memento;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;

final class ActionMementoImpl<SOURCE,TARGET>
        implements ActionMemento<SOURCE,TARGET> {

    private final Color color;
    private final Action.Type actionType;
    private final SOURCE source;
    private final TARGET target;

    public ActionMementoImpl(Color color, Action.Type actionType, SOURCE source, TARGET target) {
        this.color = color;
        this.actionType = actionType;
        this.source = source;
        this.target = target;
    }

    @Override
    public Color getColor() {
        return color;
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

    @Override
    public String toString() {
        return String.format("%s(%s %s)", actionType, source, target);
    }
}