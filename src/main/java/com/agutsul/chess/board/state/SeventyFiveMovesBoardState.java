package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class SeventyFiveMovesBoardState
        extends AbstractDrawBoardState {

    private static final Logger LOGGER = getLogger(FiveFoldRepetitionBoardState.class);

    public SeventyFiveMovesBoardState(Board board, Color color) {
        super(LOGGER, Type.SEVENTY_FIVE_MOVES, board, color);
    }
}