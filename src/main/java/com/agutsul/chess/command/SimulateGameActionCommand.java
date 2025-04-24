package com.agutsul.chess.command;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.ai.ActionSimulationResult;
import com.agutsul.chess.ai.SimulationEvaluator;
import com.agutsul.chess.ai.SimulationResult;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.ai.SimulationGame;
import com.agutsul.chess.journal.Journal;

public final class SimulateGameActionCommand<V extends Comparable<V>>
        extends AbstractCommand
        implements Closeable {

    private static final Logger LOGGER = getLogger(SimulateGameActionCommand.class);

    private final SimulationGame game;

    private SimulationEvaluator<V> evaluator;
    private SimulationResult<Action<?>,V> result;

    public SimulateGameActionCommand(Board board, Journal<ActionMemento<?,?>> journal,
                                     ForkJoinPool forkJoinPool, Color color, Action<?> action) {

        this(new SimulationGame(board, journal, forkJoinPool, color, action));
    }

    private SimulateGameActionCommand(SimulationGame game) {
        super(LOGGER);
        this.game = game;
    }

    public void setSimulationEvaluator(SimulationEvaluator<V> evaluator) {
        this.evaluator = evaluator;
    }

    public SimulationResult<Action<?>,V> getSimulationResult() {
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
        if (this.evaluator == null) {
            throw new IllegalStateException("Value evaluator is not set");
        }
    }

    @Override
    protected void postExecute() throws CommandException {
        var value = this.evaluator.evaluate(this.game);
        this.result = createSimulationResult(value);
    }

    private SimulationResult<Action<?>,V> createSimulationResult(V value) {
        return new ActionSimulationResult<>(this.game.getBoard(),
                this.game.getJournal(), this.game.getAction(),
                this.game.getColor(), value
        );
    }
}