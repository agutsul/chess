package com.agutsul.chess.piece.state;

import com.agutsul.chess.Promotable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.state.State;

public interface PromotablePieceState<COLOR extends Color,
                                      PIECE extends Piece<COLOR> & Promotable>
        extends State<PIECE> {

    void promote(PIECE piece, Position position, Piece.Type pieceType);
    void unpromote(Piece<COLOR> piece);
}
