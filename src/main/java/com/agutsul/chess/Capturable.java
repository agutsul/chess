package com.agutsul.chess;

import com.agutsul.chess.piece.Piece;

public interface Capturable {
    void capture(Piece<?> targetPiece);
    void uncapture(Piece<?> targetPiece);
}