package com.agutsul.chess.piece;

import com.agutsul.chess.position.Position;

interface IMovable extends Movable {

    void doMove(Position poition);
    void cancelMove(Position position);
}