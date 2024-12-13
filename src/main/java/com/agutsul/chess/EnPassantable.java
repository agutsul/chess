package com.agutsul.chess;

import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

public interface EnPassantable {
    void enpassant(PawnPiece<?> targetPiece, Position targetPosition);
    void unenpassant(PawnPiece<?> targetPiece);
}