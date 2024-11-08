package com.agutsul.chess.rule.board;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.Memento;

final class CompositeBoardStateEvaluator
        implements BoardStateEvaluator<BoardState> {

    private final Board board;
    private final Journal<Memento> journal;

    private final CheckedBoardStateEvaluator checkedEvaluator;
    private final CheckMatedBoardStateEvaluator checkMatedEvaluator;
    private final StaleMatedBoardStateEvaluator staleMatedEvaluator;

    CompositeBoardStateEvaluator(Board board, Journal<Memento> journal) {
        this(board, journal,
                new CheckedBoardStateEvaluator(board),
                new CheckMatedBoardStateEvaluator(board),
                new StaleMatedBoardStateEvaluator(board)
        );
    }

    CompositeBoardStateEvaluator(Board board,
                                 Journal<Memento> journal,
                                 CheckedBoardStateEvaluator checkedEvaluator,
                                 CheckMatedBoardStateEvaluator checkMatedEvaluator,
                                 StaleMatedBoardStateEvaluator staleMatedEvaluator) {
        this.board = board;
        this.journal = journal;
        this.checkedEvaluator = checkedEvaluator;
        this.checkMatedEvaluator = checkMatedEvaluator;
        this.staleMatedEvaluator = staleMatedEvaluator;
    }

    @Override
    public BoardState evaluate(Color playerColor) {
        var checked = checkedEvaluator.evaluate(playerColor);
        if (checked.isPresent()) {
            var checkMated = checkMatedEvaluator.evaluate(playerColor);
            return checkMated.isPresent() ? checkMated.get() : checked.get();
        }

        var staleMated = staleMatedEvaluator.evaluate(playerColor);
        if (staleMated.isPresent()) {
            return staleMated.get();
        }

        // TODO: implement additional evaluators
        return new DefaultBoardState(board, playerColor);
    }
}