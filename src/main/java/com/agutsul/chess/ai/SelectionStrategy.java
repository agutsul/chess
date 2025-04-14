package com.agutsul.chess.ai;

import java.util.Optional;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;

public interface SelectionStrategy<ACTION extends Action<?>> {
    Optional<ACTION> select(Color color);
    Optional<ACTION> select(Color color, BoardState.Type boardState);
}