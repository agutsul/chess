package com.agutsul.chess.piece.state;

import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface MovablePieceState<COLOR extends Color,
                                   PIECE extends Piece<COLOR> & Movable>
        extends State<PIECE> {

    void move(PIECE piece, Position position);
    void unmove(PIECE piece, Position position);
}