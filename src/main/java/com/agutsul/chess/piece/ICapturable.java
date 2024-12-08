package com.agutsul.chess.piece;

interface ICapturable extends Capturable {

    void doCapture(Piece<?> targetPiece);
    void cancelCapture(Piece<?> targetPiece);
}