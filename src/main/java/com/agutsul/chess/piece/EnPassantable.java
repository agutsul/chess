package com.agutsul.chess.piece;

import com.agutsul.chess.position.Position;

public interface EnPassantable {
    void enpassant(PawnPiece<?> targetPiece, Position targetPosition);
    void unenpassant(PawnPiece<?> targetPiece);
}