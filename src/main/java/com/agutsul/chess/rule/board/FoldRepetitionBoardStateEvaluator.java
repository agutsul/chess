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
        extends AbstractJournalStateEvaluator {

    static final int THREE_REPETITIONS = 3;
    static final int FIVE_REPETITIONS = 5;

    FoldRepetitionBoardStateEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        super(board, journal);
    }

    @Override
    AbstractBoardStateEvaluator createEvaluator(Color color) {
        return new FoldRepetitionBoardStateEvaluatorImpl(color, board, journal);
    }

    private static final class FoldRepetitionBoardStateEvaluatorImpl
            extends AbstractBoardStateEvaluator {

        private final Color color;
        private final Journal<ActionMemento<?,?>> journal;

        FoldRepetitionBoardStateEvaluatorImpl(Color color,
                                              Board board,
                                              Journal<ActionMemento<?,?>> journal) {
            super(board);
            this.color = color;
            this.journal = journal;
        }

        @Override
        public Optional<BoardState> evaluate(Color playerColor) {
            var actions = journal.get(playerColor);
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
                stats.put(code, currentStat + 1);
            }

            return stats;
        }
    }
}