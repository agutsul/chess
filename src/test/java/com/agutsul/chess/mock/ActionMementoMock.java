package com.agutsul.chess.mock;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.Action.Type;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.color.Color;

public class ActionMementoMock<SOURCE,TARGET>
        implements ActionMemento<SOURCE,TARGET> {

    private final Color color;
    private final Action.Type actionType;
    private final SOURCE source;
    private final TARGET target;

    public ActionMementoMock(Color color, Action.Type actionType, SOURCE source, TARGET target) {
        this.color = color;
        this.actionType = actionType;
        this.source = source;
        this.target = target;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public Type getActionType() {
        return this.actionType;
    }

    @Override
    public SOURCE getSource() {
        return this.source;
    }

    @Override
    public TARGET getTarget() {
        return this.target;
    }
}