package com.agutsul.chess.board.state;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

abstract class AbstractPlayableBoardState
        extends AbstractBoardState
        implements PlayableBoardState {

    private final Logger logger;

    AbstractPlayableBoardState(Logger logger, Type type, Board board, Color color) {
        super(type, board, color);
        this.logger = logger;
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        logger.info("Getting actions for piece '{}'", piece);
        return piece.getActions();
    }
}