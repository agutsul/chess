package com.agutsul.chess.board.state;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface BoardState extends State<Board> {

    enum Type {
        DEFAULT,
        CHECKED,
        CHECK_MATED,
        STALE_MATED
    }

    Type getType();
    Color getColor();

    Collection<Action<?>> getActions(Piece<Color> piece);
    Collection<Impact<?>> getImpacts(Piece<Color> piece);
}