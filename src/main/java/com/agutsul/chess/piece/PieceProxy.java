package com.agutsul.chess.piece;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.impact.Impact;
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
    public final PieceState<Piece<Color>> getState() {
        return origin.getState();
    }

    @Override
    public Collection<Action<?>> getActions() {
        return origin.getActions();
    }

    @Override
    public Collection<Impact<?>> getImpacts() {
        return origin.getImpacts();
    }
}