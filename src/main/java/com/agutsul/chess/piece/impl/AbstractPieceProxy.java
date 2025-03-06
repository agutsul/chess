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
    public final PIECE getOrigin() {
        return this.origin;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PieceState<Piece<Color>> getState() {
        return (PieceState<Piece<Color>>) (PieceState<?>) this.origin.getState();
    }

    @Override
    public Collection<Action<?>> getActions() {
        return this.origin.getActions();
    }

    @Override
    public Collection<Action<?>> getActions(Action.Type actionType) {
        return this.origin.getActions(actionType);
    }

    @Override
    public Collection<Impact<?>> getImpacts() {
        return origin.getImpacts();
    }

    @Override
    public Collection<Impact<?>> getImpacts(Impact.Type impactType) {
        return this.origin.getImpacts(impactType);
    }

    @Override
    public final Type getType() {
        return this.origin.getType();
    }

    @Override
    public final Color getColor() {
        return this.origin.getColor();
    }

    @Override
    public final String getUnicode() {
        return this.origin.getUnicode();
    }

    @Override
    public final int getValue() {
        return this.origin.getValue();
    }

    @Override
    public final int getDirection() {
        return this.origin.getDirection();
    }

    @Override
    public final Position getPosition() {
        return this.origin.getPosition();
    }

    @Override
    public List<Position> getPositions() {
        return this.origin.getPositions();
    }

    @Override
    public final boolean isActive() {
        return this.origin.isActive();
    }

    @Override
    public final boolean isMoved() {
        return this.origin.isMoved();
    }

    @Override
    public final boolean isProtected() {
        return this.origin.isProtected();
    }

    @Override
    public final void capture(Piece<?> targetPiece) {
        this.origin.capture(targetPiece);
    }

    @Override
    public final void uncapture(Piece<?> targetPiece) {
        this.origin.uncapture(targetPiece);
    }

    @Override
    public final void move(Position position) {
        this.origin.move(position);
    }

    @Override
    public final void unmove(Position position) {
        this.origin.unmove(position);
    }

    @Override
    public final int hashCode() {
        return this.origin.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this.origin.equals(obj);
    }

    @Override
    public final String toString() {
        return this.origin.toString();
    }
}