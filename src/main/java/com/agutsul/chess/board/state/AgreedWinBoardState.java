package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class AgreedWinBoardState
        extends AbstractDrawBoardState {

    private static final Logger LOGGER = getLogger(AgreedWinBoardState.class);

    public AgreedWinBoardState(Board board, Color color) {
        super(LOGGER, BoardState.Type.AGREED_WIN, board, color);
    }
}