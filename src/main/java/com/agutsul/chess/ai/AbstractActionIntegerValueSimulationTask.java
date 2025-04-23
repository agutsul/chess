package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.ai.SimulationGame;
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionIntegerValueSimulationTask
        extends AbstractActionValueSimulationTask<Integer> {

    private static final long serialVersionUID = 1L;

    AbstractActionIntegerValueSimulationTask(Logger logger, Board board,
                                             Journal<ActionMemento<?,?>> journal,
                                             ForkJoinPool forkJoinPool, List<Action<?>> actions,
                                             Color color, int limit) {

        super(logger, board, journal, forkJoinPool, actions, color, limit);
    }

    @Override
    protected ActionSimulationResult<Integer>
            select(List<ActionSimulationResult<Integer>> actionValues) {

        return ActionSelectionFunction.of(this.color).apply(actionValues);
    }

    static abstract class AbstractIntegerGameEvaluator
            extends AbstractSimulationGameEvaluator<Integer> {

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
                    ? 1000 * value * sourcePiece.getDirection()
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