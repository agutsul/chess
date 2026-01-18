package com.agutsul.chess.board.state;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface CheckableBoardState
        extends State<Board> {

    Piece<Color> getPiece();
}