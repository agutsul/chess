package com.agutsul.chess.piece.state;

import com.agutsul.chess.Checkable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface CheckMatedPieceState<PIECE extends Piece<?> & Checkable>
        extends State<PIECE> {

}