package com.agutsul.chess.board.state;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.Piece;

abstract class AbstractBoardState implements BoardState {

    protected final Type type;
    protected final Board board;
    protected final Color color;

    AbstractBoardState(Type type, Board board, Color color) {
        this.type = type;
        this.board = board;
        this.color = color;
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<Color> piece) {
        return piece.getImpacts();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.name();
    }
}