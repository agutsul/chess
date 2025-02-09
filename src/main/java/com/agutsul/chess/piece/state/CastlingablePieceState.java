package com.agutsul.chess.piece.state;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface CastlingablePieceState<PIECE extends Piece<?> & Castlingable>
        extends State<PIECE> {

    void castling(PIECE piece, Position position);
    void uncastling(PIECE piece, Position position);
}