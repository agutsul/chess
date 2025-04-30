package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.ai.SimulationGame;
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionIntegerValueSimulationTask
        extends AbstractActionValueSimulationTask<Integer> {

    private static final long serialVersionUID = 1L;

    AbstractActionIntegerValueSimulationTask(Logger logger, Board board,
                                             Journal<ActionMemento<?,?>> journal,
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

        private static final int CHECK_MATE_COEF = 1000;

        AbstractIntegerGameEvaluator(int limit) {
            super(limit);
        }

        @Override
        public final Integer evaluate(SimulationGame game) {
            var action = game.getAction();
            var sourcePiece = action.getPiece();

            var board = game.getBoard();
            var boardState = board.getState();

            var value = calculateValue(board, action, game.getColor());
            return boardState.isType(CHECK_MATED)
                    ? CHECK_MATE_COEF * value * sourcePiece.getDirection()
                    : boardState.getType().rank() * value;
        }

        protected int calculateValue(Board board, Action<?> action, Color color) {
            var sourcePiece = action.getPiece();
            var direction = sourcePiece.getDirection();

            var currentPlayerValue = board.calculateValue(color) * direction;
            var opponentPlayerValue = board.calculateValue(color.invert()) * Math.negateExact(direction);

            var value = action.getValue()                       // action type influence
                    + this.limit * direction                    // depth influence
                    + currentPlayerValue + opponentPlayerValue; // current board value

            return value;
        }
    }
}