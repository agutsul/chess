package com.agutsul.chess;

import java.util.Collection;

import com.agutsul.chess.position.Position;

public interface Castlingable {
    enum Side {
        KING,
        QUEEN
    }

    void castling(Position position);
    void uncastling(Position position);

    Collection<Side> getSides();
}