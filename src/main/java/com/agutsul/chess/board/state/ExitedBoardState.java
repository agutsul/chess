package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class ExitedBoardState
        extends AbstractTerminalBoardState {

    private static final Logger LOGGER = getLogger(ExitedBoardState.class);

    public ExitedBoardState(Board board, Color color) {
        super(LOGGER, BoardState.Type.EXITED, board, color);
    }
}