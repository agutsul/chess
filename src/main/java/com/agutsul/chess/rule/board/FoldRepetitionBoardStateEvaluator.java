package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.fiveFoldRepetitionBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.threeFoldRepetitionBoardState;
import static java.util.Map.Entry.comparingByValue;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.FiveFoldRepetitionBoardState;
import com.agutsul.chess.board.state.ThreeFoldRepetitionBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

final class FoldRepetitionBoardStateEvaluator
        extends AbstractJournalStateEvaluator {

    private static final Logger LOGGER = getLogger(FoldRepetitionBoardStateEvaluator.class);

    FoldRepetitionBoardStateEvaluator(Board board,
                                      Journal<ActionMemento<?,?>> journal) {
        super(board, journal);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' piece action repetitions", color);

        var journalActions = journal.get(color);
        if (journalActions.size() < ThreeFoldRepetitionBoardState.REPETITIONS) {
            return Optional.empty();
        }

        var stats = calculateStatistics(journalActions);

        var maxEntry = stats.entrySet().stream().max(comparingByValue());
        if (maxEntry.isEmpty()) {
            return Optional.empty();
        }

        var maxRepetitions = maxEntry.map(Map.Entry::getValue).orElse(0);
        if (maxRepetitions >= FiveFoldRepetitionBoardState.REPETITIONS) {
            var actionMemento = findActionMemento(journalActions, maxEntry);
            return Optional.of(fiveFoldRepetitionBoardState(board, actionMemento));
        }

        if (maxRepetitions >= ThreeFoldRepetitionBoardState.REPETITIONS) {
            var actionMemento = findActionMemento(journalActions, maxEntry);
            return Optional.of(threeFoldRepetitionBoardState(board, actionMemento));
        }

        return Optional.empty();
    }

    private static ActionMemento<?,?> findActionMemento(Collection<ActionMemento<?,?>> actions,
                                                        Optional<Map.Entry<String,Integer>> entry) {

        return Stream.of(actions)
                .flatMap(Collection::stream)
                .filter(action -> Stream.of(entry)
                        .flatMap(Optional::stream)
                        .map(Map.Entry::getKey)
                        .anyMatch(actionCode -> Objects.equals(createActionCode(action), actionCode))
                )
                .findFirst()
                .get();
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
                nonNull(pieceType) ? pieceType.name() : "?",
                String.valueOf(action.getTarget())
        );
    }
}