package com.agutsul.chess.board.state;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class AgreedWinBoardState
        extends AbstractBoardState {

    private static final Logger LOGGER = getLogger(AgreedWinBoardState.class);

    public AgreedWinBoardState(Board board, Color color) {
        super(BoardState.Type.AGREED_WIN, board, color);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        LOGGER.info("Getting actions for piece '{}'", piece);
        return emptyList();
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        LOGGER.info("Getting impacts for piece '{}'", piece);
        return emptyList();
    }
}