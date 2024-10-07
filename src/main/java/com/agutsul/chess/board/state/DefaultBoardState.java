package com.agutsul.chess.board.state;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Piece;

public final class DefaultBoardState extends AbstractBoardState {

    public DefaultBoardState(Board board, Color color) {
        super(BoardState.Type.DEFAULT, board, color);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        return piece.getActions();
    }
}