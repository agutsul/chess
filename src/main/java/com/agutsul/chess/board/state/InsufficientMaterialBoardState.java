package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

public final class InsufficientMaterialBoardState
        extends AbstractPlayableBoardState {

    private static final Logger LOGGER = getLogger(InsufficientMaterialBoardState.class);

    private final String source;

    public InsufficientMaterialBoardState(Board board, Color color, String source) {
        super(LOGGER, BoardState.Type.INSUFFICIENT_MATERIAL, board, color);
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}