package com.agutsul.chess.piece.state;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface EnPassantablePieceState<PIECE extends PawnPiece<?>>
        extends State<PIECE> {

    void enPassant(PIECE piece, PawnPiece<Color> targetPiece, Position targetPosition);
}