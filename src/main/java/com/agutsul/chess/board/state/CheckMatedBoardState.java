package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class CheckMatedBoardState
        extends AbstractDrawBoardState {

    private static final Logger LOGGER = getLogger(CheckMatedBoardState.class);

    public CheckMatedBoardState(Board board, Color checkMatedColor) {
        super(LOGGER, BoardState.Type.CHECK_MATED, board, checkMatedColor);
    }
}