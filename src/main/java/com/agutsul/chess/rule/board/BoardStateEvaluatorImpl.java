package com.agutsul.chess.rule.board;

import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

public final class BoardStateEvaluatorImpl
        implements BoardStateEvaluator<BoardState> {

    private final BoardStateEvaluator<BoardState> evaluator;

    public BoardStateEvaluatorImpl(Board board, Journal<ActionMemento<?,?>> journal) {
        this.evaluator = new CachedBoardStateEvaluator(board,
                new CompositeBoardStateEvaluator(board, journal)
        );
    }

    @Override
    public BoardState evaluate(Color color) {
        return evaluator.evaluate(color);
    }
}