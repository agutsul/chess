package com.agutsul.chess;

import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface Promotable {
    default void promote(Position position, Piece.Type pieceType) {};
}