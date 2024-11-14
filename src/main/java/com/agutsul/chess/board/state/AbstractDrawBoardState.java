package com.agutsul.chess.board.state;

import static java.util.Collections.emptyList;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.Piece;

abstract class AbstractDrawBoardState
        extends AbstractBoardState {

    private final Logger logger;

    AbstractDrawBoardState(Logger logger, Type type, Board board, Color color) {
        super(type, board, color);
        this.logger = logger;
    }

    @Override
    public final Collection<Action<?>> getActions(Piece<Color> piece) {
        logger.info("Getting actions for piece '{}'", piece);
        return emptyList();
    }

    @Override
    public final Collection<Impact<?>> getImpacts(Piece<Color> piece) {
        logger.info("Getting impacts for piece '{}'", piece);
        return emptyList();
    }
}