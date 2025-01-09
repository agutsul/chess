package com.agutsul.chess.board.state;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

abstract class AbstractDrawBoardState
        extends AbstractTerminalBoardState {

    AbstractDrawBoardState(Logger logger, Type type, Board board, Color color) {
        super(logger, type, board, color);
    }
}