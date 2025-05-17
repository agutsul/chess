package com.agutsul.chess.board.state;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.state.State;

public interface InsufficientMaterialBoardState
        extends State<Board> {

    enum Pattern {
        SINGLE_KING,
        KING_AND_BLOCKED_PAWNS,
        KING_VS_KING,
        KING_AND_BISHOP_VS_KING,
        KING_AND_KNIGHT_VS_KING,
        BISHOP_POSITION_COLOR_VS_KING_POSITION_COLOR,
        KING_AND_DOUBLE_KNIGHTS_VS_KING,
        KING_AND_BISHOP_VS_KING_AND_KNIGHT,
        NO_ACTIONS_LEAD_TO_CHECKMATE
    }

    Pattern getPattern();
}