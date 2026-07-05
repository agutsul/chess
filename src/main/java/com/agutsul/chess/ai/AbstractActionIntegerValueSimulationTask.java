package com.agutsul.chess.ai;

import static com.agutsul.chess.activity.action.Action.isCastling;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;

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
import com.agutsul.chess.color.Colors;
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
            var action = game.getAction();
            var sourcePiece = action.getPiece();

            var board = game.getBoard();
            var boardState = board.getState();

            var value = calculateValue(board, action, game.getColor()) * boardState.getValue();
            return boardState.isType(CHECK_MATED)
                    ? value * sourcePiece.getDirection()
                    : value;
        }

        protected int calculateValue(Board board, Action<?> action, Color color) {
            var sourcePiece = action.getPiece();

            var materialValue = Stream.of(Colors.values())
                    .mapToInt(playerColor -> board.calculateValue(color))
                    .sum();

            // calculate impact for action piece on its new position
            var impactValue = isCastling(action)
                    ? calculateImpact(board, (PieceCastlingAction<?,?,?>) action)
                    : calculateImpact(board, getActionPiece(board, action));

            var value = action.getValue()                       // action type influence
                    + impactValue                               // impacts influence
                    + this.limit * sourcePiece.getDirection()   // depth influence
                    + materialValue;

            return value;
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
/*
            var startTimepoint = System.currentTimeMillis();

            var impacts = board.getImpacts(targetPiece.get());
            logger.error("Loading '{}' impacts: {}ms",
                    targetPiece.get(), (System.currentTimeMillis() - startTimepoint)
            );

            startTimepoint = System.currentTimeMillis();

            int value = 0;
            for (var impact : impacts) {
                var valStartTimepoint = System.currentTimeMillis();

                var val = impact.getValue();

//                logger.error("Calc '{}' impact value '{}': {}ms",
//                        impact.getType(), val, (System.currentTimeMillis() - valStartTimepoint)
//                );

                value += val;
            }

            logger.error("Calculating '{}' impact value - '{}': {}ms",
                    targetPiece.get(), value, (System.currentTimeMillis() - startTimepoint)
            );

            return value;
*/

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