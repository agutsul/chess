package com.agutsul.chess;

import com.agutsul.chess.position.Position;

public interface Castlingable {
    void castling(Position position);
    void uncastling(Position position);
}