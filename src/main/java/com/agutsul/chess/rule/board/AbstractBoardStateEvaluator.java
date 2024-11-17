package com.agutsul.chess.rule.board;

import java.util.Optional;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;

abstract class AbstractBoardStateEvaluator
        implements StateEvaluator<Optional<BoardState>> {

    protected final Board board;

    AbstractBoardStateEvaluator(Board board) {
        this.board = board;
    }
}