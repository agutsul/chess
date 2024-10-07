package com.agutsul.chess.piece;

import com.agutsul.chess.position.Position;

public interface Promotable {
    void promote(Position position, Piece.Type pieceType);
}