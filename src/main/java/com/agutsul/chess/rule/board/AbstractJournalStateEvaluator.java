package com.agutsul.chess.rule.board;

import java.util.Optional;

import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

abstract class AbstractJournalStateEvaluator
        extends AbstractBoardStateEvaluator {

    protected final Journal<ActionMemento<?,?>> journal;

    AbstractJournalStateEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        super(board);
        this.journal = journal;
    }

    @Override
    public final Optional<BoardState> evaluate(Color color) {
        var evaluator = createEvaluator(color);

        var boardState = evaluator.evaluate(color);
        if (boardState.isEmpty()) {
            return evaluator.evaluate(color.invert());
        }

        var state = boardState.get();
        if (state.isTerminal()) {
            return boardState;
        }

        var opponentBoardState = evaluator.evaluate(color.invert());
        if (opponentBoardState.isEmpty()) {
            return boardState;
        }

        var opponentState = opponentBoardState.get();
        return opponentState.isTerminal() ? opponentBoardState : boardState;
    }

    abstract AbstractBoardStateEvaluator createEvaluator(Color color);
}
