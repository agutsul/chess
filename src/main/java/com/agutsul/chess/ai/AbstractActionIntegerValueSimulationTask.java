package com.agutsul.chess.ai;

import static com.agutsul.chess.activity.action.Action.isCastling;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.ai.SimulationGame;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;

abstract class AbstractActionIntegerValueSimulationTask
        extends AbstractActionValueSimulationTask<Integer> {

    private static final long serialVersionUID = 1L;

    protected static final ResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> TERMINAL_BOARD_STATE_RESULT_MATCHER =
            new TerminalBoardStateResultMatcher<>();

    AbstractActionIntegerValueSimulationTask(Logger logger, Board board, Journal<ActionMemento<?,?>> journal,
                                             ForkJoinPool forkJoinPool, List<Action<?>> actions,
                                             Color color, int limit,
                                             ResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> resultMatcher) {

        super(logger, board, journal, forkJoinPool, actions, color, limit, resultMatcher);
    }

    @Override
    protected TaskResult<Action<?>,Integer>
            select(List<TaskResult<Action<?>,Integer>> actionValues) {

        return ActionSelectionFunction.of(this.color).apply(actionValues);
    }

    @Override
    protected ActionSimulationResult<Integer> createTaskResult(Action<?> action, Integer value) {

        return new ActionSimulationResult<>(this.board, this.journal, action, this.color, value);
    }

    @Override
    protected ActionSimulationResult<Integer> createTaskResult(Game game, Action<?> action, Integer value) {

        return new ActionSimulationResult<>(game.getBoard(), game.getJournal(), action, color, value);
    }

    @Override
    protected ActionSimulationResult<Integer> createTaskResult(TaskResult<Action<?>,Integer> result,
                                                               Color color, Integer value) {

        return new ActionSimulationResult<>(result.getBoard(), result.getJournal(), null, color, value);
    }

    static abstract class AbstractIntegerGameEvaluator
            extends AbstractSimulationGameEvaluator<Integer> {

        protected final Logger logger;

        AbstractIntegerGameEvaluator(Logger logger, int limit) {
            super(limit);
            this.logger = logger;
        }

        @Override
        public final Integer evaluate(SimulationGame game) {
            return calculateValue(game.getBoard(), game.getAction(), game.getColor());
        }

        protected int calculateValue(Board board, Action<?> action, Color color) {
            var materialValue = board.calculateValue(color);

            // calculate impact for action piece on its new position
            var impactValue = isCastling(action)
                    ? calculateImpact(board, (PieceCastlingAction<?,?,?>) action)
                    : calculateImpact(board, getActionPiece(board, action));

            var value = action.getValue()                           // action type influence
                    + impactValue                                   // impacts influence
                    + this.limit * action.getPiece().getDirection() // depth influence
                    + materialValue;

            return value * board.getState().getValue();
        }

        private static int calculateImpact(Board board, PieceCastlingAction<?,?,?> action) {
            var value = Stream.of(action.getSource(), action.getTarget())
                    .parallel()
                    .map(moveAction -> getActionPiece(board, moveAction))
                    .mapToInt(piece -> calculateImpact(board, piece))
                    .sum();

            return value;
        }

        private static int calculateImpact(Board board, Optional<Piece<Color>> targetPiece) {
            var value = Stream.of(targetPiece)
                    .flatMap(Optional::stream)
                    .map(piece -> board.getImpacts(piece))
                    .flatMap(Collection::parallelStream)
                    .mapToInt(Impact::getValue)
                    .sum();

            return value;
        }

        private static Optional<Piece<Color>> getActionPiece(Board board, Action<?> action) {
            // same source piece but located on target position after executing an action
            return board.getPiece(action.getPosition());
        }
    }
}