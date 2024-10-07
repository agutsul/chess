package com.agutsul.chess.piece.state;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface MovablePieceState<PIECE extends Piece<Color> & Movable>
        extends State<PIECE> {

    void move(PIECE piece, Position position);
}