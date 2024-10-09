package com.agutsul.chess.piece;

public interface Capturable {
    void capture(Piece<?> targetPiece);
}