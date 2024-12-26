package com.agutsul.chess.piece.state;

import com.agutsul.chess.Checkable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface CheckedPieceState<COLOR extends Color,
                                   PIECE extends Piece<COLOR> & Checkable>
        extends State<PIECE> {

}