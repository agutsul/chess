package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.fiveFoldRepetitionBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.threeFoldRepetitionBoardState;
import static java.util.Map.Entry.comparingByValue;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

final class FoldRepetitionBoardStateEvaluator
        extends AbstractJournalStateEvaluator {

    private static final Logger LOGGER = getLogger(FoldRepetitionBoardStateEvaluator.class);

    static final int THREE_REPETITIONS = 3;
    static final int FIVE_REPETITIONS = 5;

    FoldRepetitionBoardStateEvaluator(Board board,
                                      Journal<ActionMemento<?,?>> journal) {
        super(board, journal);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' piece action repetitions", color);

        if (journal.size(color) < THREE_REPETITIONS) {
            return Optional.empty();
        }

        var journalActions = journal.get(color);

        var map = new HashMap<String,ActionMemento<?,?>>();
        for (var action : journalActions) {
            map.put(createActionCode(action), action);
        }

        var stats = calculateStatistics(journalActions);

        var maxEntry = stats.entrySet().stream().max(comparingByValue());
        if (maxEntry.isEmpty()) {
            return Optional.empty();
        }

        var maxRepetitions = maxEntry.map(Map.Entry::getValue).orElse(0);
        if (maxRepetitions >= FIVE_REPETITIONS) {
            var actionMemento = map.get(maxEntry.get().getKey());
            return Optional.of(fiveFoldRepetitionBoardState(board, actionMemento));
        }

        if (maxRepetitions >= THREE_REPETITIONS) {
            var actionMemento = map.get(maxEntry.get().getKey());
            return Optional.of(threeFoldRepetitionBoardState(board, actionMemento));
        }

        return Optional.empty();
    }

    private static Map<String,Integer> calculateStatistics(Collection<ActionMemento<?,?>> actions) {
        var stats = new HashMap<String,Integer>();
        for (var action : actions) {
            var actionCode = createActionCode(action);

            var currentStat = stats.getOrDefault(actionCode, 0);
            stats.put(actionCode, currentStat + 1);
        }

        return stats;
    }

    private static String createActionCode(ActionMemento<?,?> action) {
        var pieceType = action.getPieceType();
        return String.format("%s_%s",
                pieceType.name(),
                String.valueOf(action.getTarget())
        );
    }
}