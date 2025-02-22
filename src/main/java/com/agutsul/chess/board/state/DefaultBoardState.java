package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class DefaultBoardState
        extends AbstractPlayableBoardState {

    private static final Logger LOGGER = getLogger(DefaultBoardState.class);

    public DefaultBoardState(Board board, Color color) {
        super(LOGGER, BoardState.Type.DEFAULT, board, color);
    }
}