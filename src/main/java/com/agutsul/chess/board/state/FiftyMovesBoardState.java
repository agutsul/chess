package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class FiftyMovesBoardState
        extends AbstractPlayableBoardState {

    private static final Logger LOGGER = getLogger(FiftyMovesBoardState.class);

    // draw
    public FiftyMovesBoardState(Board board, Color color) {
        super(LOGGER, Type.FIFTY_MOVES, board, color);
    }
}