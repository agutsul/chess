package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.fiftyMovesBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.seventyFiveMovesBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.SetActionCounterEvent;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.FiftyMovesBoardState;
import com.agutsul.chess.board.state.SeventyFiveMovesBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.statistic.JournalActionCalculation;

final class MovesBoardStateEvaluator
        extends AbstractJournalStateEvaluator {

    private static final Logger LOGGER = getLogger(MovesBoardStateEvaluator.class);

    private ActionCountMatcher actionCountMatcher;

    MovesBoardStateEvaluator(Board board,
                             Journal<ActionMemento<?,?>> journal) {

        super(board, journal);

        setActionCountMatcher(new JournalActionCountMatcher(board, journal));

        ((Observable) board).addObserver(new ActionCounterChangeObserver());
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' missed any capture or pawn moves", color);

        var performedActions = journal.size(color);
        if (performedActions < FiftyMovesBoardState.MOVES) {
            return Optional.empty();
        }

        if (performedActions >= SeventyFiveMovesBoardState.MOVES
                && actionCountMatcher.match(color, SeventyFiveMovesBoardState.MOVES)) {

            return Optional.of(seventyFiveMovesBoardState(board, color));
        }

        if (performedActions >= FiftyMovesBoardState.MOVES
                && actionCountMatcher.match(color, FiftyMovesBoardState.MOVES)) {

            return Optional.of(fiftyMovesBoardState(board, color));
        }

        return Optional.empty();
    }

    private void setActionCountMatcher(ActionCountMatcher matcher) {
        this.actionCountMatcher = matcher;
    }

    private final class ActionCounterChangeObserver
            extends AbstractEventObserver<SetActionCounterEvent> {

        @Override
        protected void process(SetActionCounterEvent event) {
            setActionCountMatcher(new HalfMoveActionCountMatcher(
                    board, journal, event.getCounter()
            ));
        }
    }

    interface ActionCountMatcher {
        boolean match(Color color, int limit);
    }

    private static abstract class AbstractActionCountMatcher
            implements ActionCountMatcher {

        protected final Board board;
        protected final Journal<ActionMemento<?,?>> journal;

        AbstractActionCountMatcher(Board board, Journal<ActionMemento<?,?>> journal) {
            this.board = board;
            this.journal = journal;
        }

        protected final int calculate(Color color, int limit) {
            var calculationTask = new JournalActionCalculation(board, journal);
            return calculationTask.calculate(color, limit);
        }
    }

    private static final class HalfMoveActionCountMatcher
            extends AbstractActionCountMatcher {

        private static final Logger LOGGER = getLogger(HalfMoveActionCountMatcher.class);

        private final int halfMoveCounter;

        HalfMoveActionCountMatcher(Board board, Journal<ActionMemento<?,?>> journal,
                                   int halfMoveCounter) {

            super(board, journal);
            this.halfMoveCounter = halfMoveCounter;
        }

        @Override
        public boolean match(Color color, int limit) {
            var results = calculate(color, limit);
            if (results < 0) {
                LOGGER.error("Half move journal action counter failed");
                return false;
            }

            return results + this.halfMoveCounter >= limit;
        }
    }

    private static final class JournalActionCountMatcher
            extends AbstractActionCountMatcher {

        private static final Logger LOGGER = getLogger(JournalActionCountMatcher.class);

        public JournalActionCountMatcher(Board board, Journal<ActionMemento<?,?>> journal) {
            super(board, journal);
        }

        @Override
        public boolean match(Color color, int limit) {
            var results = calculate(color, limit);
            if (results < 0) {
                LOGGER.error("Journal action counter failed");
                return false;
            }

            return results == limit;
        }
    }
}