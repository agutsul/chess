package com.agutsul.chess.rule.board;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.FiveFoldRepetitionBoardState;
import com.agutsul.chess.board.state.ThreeFoldRepetitionBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

final class FoldRepetitionBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final int THREE_REPETITIONS = 3;
    private static final int FIVE_REPETITIONS = 5;

    private final Journal<ActionMemento<?,?>> journal;

    FoldRepetitionBoardStateEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        super(board);
        this.journal = journal;
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        var actions = journal.get(color);
        if (actions.size() < THREE_REPETITIONS) {
            return Optional.empty();
        }

        var stats = calculateStatistics(actions);
        var maxRepetitions = stats.entrySet().stream()
                .mapToInt(Entry::getValue)
                .max()
                .orElse(0);

        if (maxRepetitions >= FIVE_REPETITIONS) {
            return Optional.of(new FiveFoldRepetitionBoardState(board, color));
        }

        if (maxRepetitions >= THREE_REPETITIONS) {
            return Optional.of(new ThreeFoldRepetitionBoardState(board, color));
        }

        return Optional.empty();
    }

    private static Map<String,Integer> calculateStatistics(Collection<ActionMemento<?,?>> actions) {
        var stats = new HashMap<String,Integer>();
        for (var action : actions) {
            var code = String.format("%s_%s",
                    action.getPieceType().name(),
                    String.valueOf(action.getTarget())
            );

            var currentStat = stats.getOrDefault(code, 0);
            var newStat = currentStat + 1;

            stats.put(code, newStat);
        }

        return stats;
    }
}