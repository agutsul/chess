package com.agutsul.chess.piece.state;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Castlingable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface CastlingablePieceState<COLOR extends Color,
                                        PIECE extends Piece<COLOR> & Castlingable>
        extends State<PIECE> {

    void castling(PIECE piece, Position position);
    void uncastling(PIECE piece, Position position);
}