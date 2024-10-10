package com.agutsul.chess.board.state;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.Piece;

public final class StaleMatedBoardState
        extends AbstractBoardState {

    private static final Logger LOGGER = getLogger(StaleMatedBoardState.class);

    public StaleMatedBoardState(Board board, Color checkMatedColor) {
        super(BoardState.Type.STALE_MATED, board, checkMatedColor);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        LOGGER.info("Getting actions for piece '{}'", piece);
        return emptyList();
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<Color> piece) {
        LOGGER.info("Getting impacts for piece '{}'", piece);
        return emptyList();
    }
}