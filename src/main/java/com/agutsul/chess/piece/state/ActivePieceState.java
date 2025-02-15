package com.agutsul.chess.piece.state;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface ActivePieceState<PIECE extends Piece<?> & Movable & Capturable>
        extends State<PIECE> {

}