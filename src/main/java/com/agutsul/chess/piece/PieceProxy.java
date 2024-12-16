package com.agutsul.chess.piece;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

class PieceProxy implements Piece<Color> {

    protected Piece<Color> origin;

    PieceProxy(Piece<Color> origin) {
        this.origin = origin;
    }

    @Override
    public final Type getType() {
        return origin.getType();
    }

    @Override
    public final Color getColor() {
        return origin.getColor();
    }

    @Override
    public final String getUnicode() {
        return origin.getUnicode();
    }

    @Override
    public final int getValue() {
        return origin.getValue();
    }

    @Override
    public final Position getPosition() {
        return origin.getPosition();
    }

    @Override
    public final boolean isMoved() {
        return origin.isMoved();
    }

    @Override
    public final boolean isActive() {
        return origin.isActive();
    }

    @Override
    public final List<Position> getPositions() {
        return origin.getPositions();
    }

    @Override
    public PieceState<Color,Piece<Color>> getState() {
        return origin.getState();
    }

    @Override
    public Collection<Action<?>> getActions() {
        return origin.getActions();
    }

    @Override
    public Collection<Action<?>> getActions(Action.Type actionType) {
        return origin.getActions(actionType);
    }

    @Override
    public Collection<Impact<?>> getImpacts() {
        return origin.getImpacts();
    }

    @Override
    public Collection<Impact<?>> getImpacts(Impact.Type impactType) {
        return origin.getImpacts(impactType);
    }

    @Override
    public String toString() {
        return origin.toString();
    }

    @Override
    public int hashCode() {
        return origin.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return origin.equals(obj);
    }
}