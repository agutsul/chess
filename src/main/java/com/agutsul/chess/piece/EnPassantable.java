package com.agutsul.chess.piece;

import com.agutsul.chess.position.Position;

public interface EnPassantable {
    void enPassant(PawnPiece<?> targetPiece, Position targetPosition);
}