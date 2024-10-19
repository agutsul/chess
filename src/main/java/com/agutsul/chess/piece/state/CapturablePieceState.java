package com.agutsul.chess.piece.state;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface CapturablePieceState<PIECE extends Piece<Color> & Capturable>
        extends State<PIECE> {

    void capture(PIECE piece, Piece<?> targetPiece);
    void uncapture(PIECE piece, Piece<?> targetPiece);
}