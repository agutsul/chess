package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class DefaultBoardState
        extends AbstractBoardState {

    private static final Logger LOGGER = getLogger(DefaultBoardState.class);

    public DefaultBoardState(Board board, Color color) {
        super(BoardState.Type.DEFAULT, board, color);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        LOGGER.info("Getting actions for piece '{}'", piece);
        return piece.getActions();
    }
}