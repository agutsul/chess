package com.agutsul.chess.command;

import static java.util.Objects.isNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.ai.SimulationEvaluator;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.ai.SimulationGame;
import com.agutsul.chess.journal.Journal;

public final class SimulateGameActionCommand<VALUE extends Comparable<VALUE>>
        extends AbstractCommand
        implements Closeable, Valuable<VALUE> {

    private static final Logger LOGGER = getLogger(SimulateGameActionCommand.class);

    private static final String VALUE_EVALUATOR_ERROR_MESSAGE = "Value evaluator is not set";

    private final SimulationGame game;

    private SimulationEvaluator<VALUE> evaluator;
    private VALUE result;

    public SimulateGameActionCommand(Board board, Journal<ActionMemento<?,?>> journal,
                                     ForkJoinPool forkJoinPool, Color color, Action<?> action) {

        this(new SimulationGame(board, journal, forkJoinPool, color, action));
    }

    private SimulateGameActionCommand(SimulationGame game) {
        super(LOGGER);
        this.game = game;
    }

    public void setSimulationEvaluator(SimulationEvaluator<VALUE> evaluator) {
        this.evaluator = evaluator;
    }

    public Game getGame() {
        return this.game;
    }

    @Override
    public VALUE getValue() {
        return this.result;
    }

    @Override
    public void close() throws IOException {
        this.game.close();
    }

    @Override
    protected void executeInternal() throws CommandException {
        this.game.run();
    }

    @Override
    protected void preExecute() throws CommandException {
        if (isNull(this.evaluator)) {
            throw new IllegalStateException(VALUE_EVALUATOR_ERROR_MESSAGE);
        }
    }

    @Override
    protected void postExecute() throws CommandException {
        this.result = this.evaluator.evaluate(this.game);
    }
}