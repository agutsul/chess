package com.agutsul.chess.piece.state;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface EnPassantablePieceState<COLOR extends Color,
                                         PIECE extends PawnPiece<COLOR>>
        extends State<PIECE> {

    void enpassant(PIECE piece, PawnPiece<?> targetPiece, Position targetPosition);
    void unenpassant(PIECE piece, PawnPiece<?> targetPiece);
}