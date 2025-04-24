package com.agutsul.chess.ai;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.command.SimulateGameActionCommand;
import com.agutsul.chess.game.ai.SimulationGame;
import com.agutsul.chess.journal.Journal;

final class BoardStateActionSelectionTask
        extends AbstractActionValueSimulationTask<Boolean> {

    private static final Logger LOGGER = getLogger(BoardStateActionSelectionTask.class);

    private static final long serialVersionUID = 1L;

    private final Color initColor;
    private final BoardState.Type boardState;

    private final BoardStateContext context;

    BoardStateActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                  ForkJoinPool forkJoinPool, Color color,
                                  BoardState.Type boardState, int limit) {

        this(board, journal, forkJoinPool, getActions(board, color),
                color, color, boardState, limit, new BoardStateContext()
        );
    }

    BoardStateActionSelectionTask(Board board, Journal<ActionMemento<?, ?>> journal,
                                  ForkJoinPool forkJoinPool, List<Action<?>> actions,
                                  Color initColor, Color color, BoardState.Type boardState,
                                  int limit, BoardStateContext context) {

        super(LOGGER, board, journal, forkJoinPool, actions, color, limit);

        this.initColor = initColor;
        this.boardState = boardState;
        this.context = context;
    }

    @Override
    public SimulationResult<Action<?>,Boolean> simulate(Action<?> action) {
        try (var command = new SimulateGameActionCommand<Boolean>(board, journal, forkJoinPool, color, action)) {
            command.setSimulationEvaluator(new BoardStateActionGameEvaluator(initColor, boardState, limit + 1));
            command.execute();

            var simulationResult = (ActionSimulationResult<Boolean>) command.getSimulationResult();
            if (isDone(simulationResult)) {
                return simulationResult;
            }

            var opponentColor = this.color.invert();

            var opponentActions = getActions(simulationResult.getBoard(), opponentColor);
            if (opponentActions.isEmpty()) {
                return simulationResult;
            }

            var opponentTask = createTask(simulationResult, opponentActions, opponentColor);
            opponentTask.fork();

            var opponentResult = opponentTask.join();

            simulationResult.setOpponentResult(opponentResult);
            simulationResult.setValue(opponentResult.getValue());

            return simulationResult;
        } catch (Exception e) {
            var message = String.format("Simulation for '%s' action '%s' failed",
                    this.color, action
            );

            logger.error(message, e);
        }

        return new ActionSimulationResult<>(board, journal, action, color, Boolean.FALSE);
    }

    @Override
    protected SimulationResult<Action<?>,Boolean>
            select(List<SimulationResult<Action<?>,Boolean>> actionValues) {

        var result = actionValues.stream()
                .filter(av -> av.getValue() && this.context.isFound())
                .findFirst();

        return result.isPresent()
                ? result.get()
                : actionValues.get(0);
    }

    @Override
    protected boolean isDone(SimulationResult<Action<?>,Boolean> simulationResult) {
        if (this.limit == 0 || this.context.isFound()) {
            return true;
        }

        var gameBoard = simulationResult.getBoard();
        var boardState = gameBoard.getState();

        var journal = simulationResult.getJournal();
        var actionMemento = journal.getLast();

        if (Objects.equals(this.initColor, actionMemento.getColor())
                && Objects.equals(this.boardState, boardState.getType())) {

            this.context.setFound(true);
        }

        return boardState.isTerminal();
    }

    @Override
    protected BoardStateActionSelectionTask createTask(List<Action<?>> actions) {
        // root level task
        return new BoardStateActionSelectionTask(this.board, this.journal,
                this.forkJoinPool, actions, this.initColor, this.color,
                this.boardState,  this.limit, this.context
        );
    }

    @Override
    protected BoardStateActionSelectionTask createTask(SimulationResult<Action<?>,Boolean> simulationResult,
                                                       List<Action<?>> actions, Color color) {
        // node level task
        return new BoardStateActionSelectionTask(simulationResult.getBoard(),
                simulationResult.getJournal(), this.forkJoinPool, actions,
                this.initColor, color, this.boardState, this.limit - 1, this.context
        );
    }

    private static final class BoardStateActionGameEvaluator
            extends AbstractSimulationGameEvaluator<Boolean> {

        private final Color color;
        private final BoardState.Type boardState;

        BoardStateActionGameEvaluator(Color color, BoardState.Type boardState, int limit) {
            super(limit);
            this.color = color;
            this.boardState = boardState;
        }

        @Override
        public Boolean evaluate(SimulationGame game) {
            var board = game.getBoard();
            var boardState = board.getState();

            var journal = game.getJournal();
            var actionMemento = journal.getLast();

            var value = Objects.equals(this.color, actionMemento.getColor())
                    && Objects.equals(this.boardState, boardState.getType());

            return value;
        }
    }

    private static final class BoardStateContext implements Serializable {

        private static final long serialVersionUID = 1L;

        private AtomicBoolean found;

        public BoardStateContext() {
            this.found = new AtomicBoolean(false);
        }

        public boolean isFound() {
            return this.found.get();
        }

        public void setFound(boolean found) {
            this.found.set(found);
        }
    }
}