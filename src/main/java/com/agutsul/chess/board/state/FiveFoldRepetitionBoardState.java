package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class FiveFoldRepetitionBoardState
        extends AbstractTerminalBoardState {

    private static final Logger LOGGER = getLogger(FiveFoldRepetitionBoardState.class);

    public FiveFoldRepetitionBoardState(Board board, Color color) {
        super(LOGGER, BoardState.Type.FIVE_FOLD_REPETITION, board, color);
    }
}