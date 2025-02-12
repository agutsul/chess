package com.agutsul.chess;

import com.agutsul.chess.position.Position;

public interface Movable {
    void move(Position position);
    void unmove(Position position);
    boolean isMoved();
}