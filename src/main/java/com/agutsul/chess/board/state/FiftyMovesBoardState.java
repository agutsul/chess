package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class FiftyMovesBoardState
        extends AbstractBoardState {

    private static final Logger LOGGER = getLogger(FiftyMovesBoardState.class);

    public FiftyMovesBoardState(Board board, Color color) {
        super(Type.FIFTY_MOVES, board, color);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        LOGGER.info("Getting actions for piece '{}'", piece);
        return piece.getActions();
    }
}