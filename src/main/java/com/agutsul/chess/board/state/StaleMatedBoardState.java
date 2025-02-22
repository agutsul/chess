package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class StaleMatedBoardState
        extends AbstractTerminalBoardState {

    private static final Logger LOGGER = getLogger(StaleMatedBoardState.class);

    // draw
    public StaleMatedBoardState(Board board, Color checkMatedColor) {
        super(LOGGER, BoardState.Type.STALE_MATED, board, checkMatedColor);
    }
}