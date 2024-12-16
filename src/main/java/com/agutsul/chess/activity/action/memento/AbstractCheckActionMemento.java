package com.agutsul.chess.activity.action.memento;

import java.time.LocalDateTime;

import com.agutsul.chess.Checkable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

abstract class AbstractCheckActionMemento<SOURCE,TARGET>
        implements ActionMemento<SOURCE,TARGET>, Checkable {

    private final ActionMemento<SOURCE,TARGET> origin;

    private final boolean checked;
    private final boolean checkMated;

    AbstractCheckActionMemento(ActionMemento<SOURCE,TARGET> origin,
                               boolean checked,
                               boolean checkMated) {
        this.origin = origin;
        this.checked = checked;
        this.checkMated = checkMated;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return origin.getCreatedAt();
    }

    @Override
    public Color getColor() {
        return origin.getColor();
    }

    @Override
    public Action.Type getActionType() {
        return origin.getActionType();
    }

    @Override
    public Piece.Type getPieceType() {
        return origin.getPieceType();
    }

    @Override
    public SOURCE getSource() {
        return origin.getSource();
    }

    @Override
    public TARGET getTarget() {
        return origin.getTarget();
    }

    @Override
    public String toString() {
        return origin.toString();
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public boolean isCheckMated() {
        return checkMated;
    }
}