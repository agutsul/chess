package com.agutsul.chess.board.state;

import java.util.Collection;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

// wrapper class used when opponent board state is returned
// but state should contain requested color
public final class BoardStateProxy
        implements BoardState {

    private BoardState origin;

    public BoardStateProxy(BoardState state) {
        this.origin = state;
    }

    public BoardState getOrigin() {
        return this.origin;
    }

    @Override
    public Color getColor() {
        return this.origin.getColor().invert();
    }

    @Override
    public Type getType() {
        return this.origin.getType();
    }

    @Override
    public boolean isType(Type type) {
        return this.origin.isType(type);
    }

    @Override
    public boolean isAnyType(Type type, Type... types) {
        return this.origin.isAnyType(type, types);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        return this.origin.getActions(piece);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        return this.origin.getImpacts(piece);
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }
}