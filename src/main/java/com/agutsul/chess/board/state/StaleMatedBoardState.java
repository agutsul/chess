package com.agutsul.chess.board.state;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.Piece;

public final class StaleMatedBoardState extends AbstractBoardState {

    public StaleMatedBoardState(Board board, Color checkMatedColor) {
        super(BoardState.Type.STALE_MATED, board, checkMatedColor);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        return emptyList();
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<Color> piece) {
        return emptyList();
    }
}