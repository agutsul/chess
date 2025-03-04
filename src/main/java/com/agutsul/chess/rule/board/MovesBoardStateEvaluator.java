package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.fiftyMovesBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.seventyFiveMovesBoardState;
import static java.util.Collections.max;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;

final class MovesBoardStateEvaluator
        extends AbstractJournalStateEvaluator {

    private static final Logger LOGGER = getLogger(MovesBoardStateEvaluator.class);

    static final int FIFTY_MOVES = 50;
    static final int SEVENTY_FIVE_MOVES = 75;

    MovesBoardStateEvaluator(Board board,
                             Journal<ActionMemento<?,?>> journal) {
        super(board, journal);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' missed any capture or pawn moves", color);

        var actions = journal.get(color);

        var performedActions = actions.size();
        if (performedActions < FIFTY_MOVES) {
            return Optional.empty();
        }

        if (performedActions >= SEVENTY_FIVE_MOVES
                && isBoardStateApplicable(actions, SEVENTY_FIVE_MOVES)) {

            return Optional.of(seventyFiveMovesBoardState(board, color));
        }

        if (performedActions >= FIFTY_MOVES
                && isBoardStateApplicable(actions, FIFTY_MOVES)) {

            return Optional.of(fiftyMovesBoardState(board, color));
        }

        return Optional.empty();
    }

    private boolean isBoardStateApplicable(List<ActionMemento<?,?>> actions,
                                           int lastMovesCount) {

        var tasks = List.of(
                new CaptureCalculationTask(actions,  lastMovesCount),
                new PawnMoveCalculationTask(actions, lastMovesCount)
        );

        try {
            var results = new ArrayList<Integer>();

            var executor = board.getExecutorService();
            for (var future : executor.invokeAll(tasks)) {
                results.add(future.get());
            }

            return max(results) == 0;
        } catch (InterruptedException e) {
            LOGGER.error("Board state evaluation interrupted", e);
        } catch (ExecutionException e) {
            LOGGER.error("Board state evaluation failed", e);
        }

        return false;
    }

    private static abstract class AbstractCalculationTask
            implements Callable<Integer> {

        protected final Logger logger;
        protected final List<ActionMemento<?,?>> actions;
        protected final int limit;

        AbstractCalculationTask(Logger logger, List<ActionMemento<?,?>> actions, int limit) {
            this.logger = logger;
            this.actions = actions;
            this.limit = limit;
        }

        @Override
        public Integer call() throws Exception {
            try {
                return calculate();
            } catch (Exception e) {
                logger.error("Board state calculation failed", e);
            }

            return 0;
        }

        protected abstract int calculate();
    }

    private static final class CaptureCalculationTask
            extends AbstractCalculationTask {

        private static final Logger LOGGER = getLogger(CaptureCalculationTask.class);

        private static final Set<Action.Type> CAPTURE_TYPES =
                EnumSet.of(Action.Type.CAPTURE, Action.Type.EN_PASSANT);

        CaptureCalculationTask(List<ActionMemento<?,?>> actions, int limit) {
            super(LOGGER, actions, limit);
        }

        @Override
        protected int calculate() {
            int counter = 0;

            for (int i = actions.size() - 1, j = 0; i >= 0 && j < limit; i--, j++) {
                var memento = actions.get(i);

                var actionType = memento.getActionType();
                if (isCapture(actionType)
                        || (Action.Type.PROMOTE.equals(actionType) && isCapture(memento))) {

                    counter++;
                }
            }

            return counter;
        }

        private static boolean isCapture(ActionMemento<?,?> memento) {
            @SuppressWarnings("unchecked")
            var promoteMemento =
                    (ActionMemento<String,ActionMemento<String,String>>) memento;

            var originAction = promoteMemento.getTarget();
            return isCapture(originAction.getActionType());
        }

        private static boolean isCapture(Action.Type actionType) {
            return CAPTURE_TYPES.contains(actionType);
        }
    }

    private static final class PawnMoveCalculationTask
            extends AbstractCalculationTask {

        private static final Logger LOGGER = getLogger(PawnMoveCalculationTask.class);

        PawnMoveCalculationTask(List<ActionMemento<?,?>> actions, int limit) {
            super(LOGGER, actions, limit);
        }

        @Override
        protected int calculate() {
            int counter = 0;

            for (int i = actions.size() - 1, j = 0; i >= 0 && j < limit; i--, j++) {
                var memento = actions.get(i);
                if (!Piece.Type.PAWN.equals(memento.getPieceType())) {
                    continue;
                }

                var actionType = memento.getActionType();
                if (isMove(actionType)
                        || (Action.Type.PROMOTE.equals(actionType) && isMove(memento))) {

                    counter++;
                }
            }

            return counter;
        }

        private static boolean isMove(ActionMemento<?,?> memento) {
            @SuppressWarnings("unchecked")
            var promoteMemento =
                    (ActionMemento<String,ActionMemento<String,String>>) memento;

            var originAction = promoteMemento.getTarget();
            return isMove(originAction.getActionType());
        }

        private static boolean isMove(Action.Type actionType) {
            return Action.Type.MOVE.equals(actionType);
        }
    }
}