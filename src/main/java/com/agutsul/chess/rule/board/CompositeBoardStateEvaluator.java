package com.agutsul.chess.rule.board;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

final class CompositeBoardStateEvaluator
        implements BoardStateEvaluator<BoardState> {

    private final Board board;
    private final List<Function<Color,Optional<BoardState>>> evaluators;

    CompositeBoardStateEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        this(board,
                new CheckedBoardStateEvaluator(board),
                new CheckMatedBoardStateEvaluator(board),
                new StaleMatedBoardStateEvaluator(board),
                new FoldRepetitionBoardStateEvaluator(board, journal)
        );
    }

    CompositeBoardStateEvaluator(Board board,
                                 CheckedBoardStateEvaluator checkedEvaluator,
                                 CheckMatedBoardStateEvaluator checkMatedEvaluator,
                                 StaleMatedBoardStateEvaluator staleMatedEvaluator,
                                 FoldRepetitionBoardStateEvaluator foldRepetitionEvaluator) {
        this.board = board;
        this.evaluators = List.of(
                color -> evaluate(color, checkedEvaluator, checkMatedEvaluator),
                color -> evaluate(color, staleMatedEvaluator),
                color -> evaluate(color, foldRepetitionEvaluator)
        );
    }

    @Override
    public BoardState evaluate(Color playerColor) {
        for (var evaluator : evaluators) {
            var boardState = evaluator.apply(playerColor);
            if (boardState.isPresent()) {
                return boardState.get();
            }
        }

        return new DefaultBoardState(board, playerColor);
    }

    private static Optional<BoardState> evaluate(Color color,
            CheckedBoardStateEvaluator checkedEvaluator,
            CheckMatedBoardStateEvaluator checkMatedEvaluator) {

        var checked = checkedEvaluator.evaluate(color);
        if (checked.isPresent()) {
            var checkMated = checkMatedEvaluator.evaluate(color);
            return checkMated.isPresent() ? checkMated : checked;
        }

        return Optional.empty();
    }

    private static Optional<BoardState> evaluate(Color color,
            BoardStateEvaluator<Optional<BoardState>> evaluator) {

        return evaluator.evaluate(color);
    }
}