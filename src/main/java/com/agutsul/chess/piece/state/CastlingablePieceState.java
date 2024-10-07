package com.agutsul.chess.piece.state;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Castlingable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface CastlingablePieceState<PIECE extends Piece<Color> & Castlingable>
        extends State<PIECE> {

    void castling(PIECE piece, Position position);
}