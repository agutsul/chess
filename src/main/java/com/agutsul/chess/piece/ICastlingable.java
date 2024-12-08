package com.agutsul.chess.piece;

import com.agutsul.chess.position.Position;

interface ICastlingable extends Castlingable {

    void doCastling(Position position);
    void cancelCastling(Position position);
}