package com.agutsul.chess.piece.state;

import com.agutsul.chess.Demotable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface TransformablePieceState<PIECE extends Piece<?>>
        extends State<PIECE> {

    <P extends Piece<?> & Promotable> void promote(P piece, Position position, Piece.Type pieceType);
    <D extends Piece<?> & Demotable>  void demote(D piece);
}