package com.agutsul.chess.piece;

import com.agutsul.chess.position.Position;

interface IEnPassantable extends EnPassantable {

    void doEnPassant(PawnPiece<?> targetPiece, Position targetPosition);
    void cancelEnPassant(PawnPiece<?> targetPiece);
}