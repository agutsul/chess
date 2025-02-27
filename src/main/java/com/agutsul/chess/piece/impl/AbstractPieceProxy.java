package com.agutsul.chess.piece.impl;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceProxy;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceProxy<PIECE extends Piece<?> & Movable & Capturable & Protectable>
        implements PieceProxy<PIECE>, Movable, Capturable, Protectable {

    protected PIECE origin;

    AbstractPieceProxy(PIECE origin) {
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
    public final List<Position> getPositions() {
        return origin.getPositions();
    }

    @Override
    public final boolean isActive() {
        return origin.isActive();
    }

    @Override
    public boolean isMoved() {
        return origin.isMoved();
    }

    @Override
    public boolean isProtected() {
        return origin.isProtected();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PieceState<Piece<Color>> getState() {
        return (PieceState<Piece<Color>>) (PieceState<?>) origin.getState();
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
    public void capture(Piece<?> targetPiece) {
        origin.capture(targetPiece);
    }

    @Override
    public void uncapture(Piece<?> targetPiece) {
        origin.uncapture(targetPiece);
    }

    @Override
    public void move(Position position) {
        origin.move(position);
    }

    @Override
    public void unmove(Position position) {
        origin.unmove(position);
    }

    @Override
    public final int hashCode() {
        return origin.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return origin.equals(obj);
    }

    @Override
    public final String toString() {
        return origin.toString();
    }
}