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
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.ai.SimulationGame;
import com.agutsul.chess.journal.Journal;

public final class SimulateGameCommand<T extends Comparable<T>>
        extends AbstractCommand
        implements Closeable {

    private static final Logger LOGGER = getLogger(SimulateGameCommand.class);

    private final SimulationGame game;

    private SimulationEvaluator<T> evaluator;
    private ActionSimulationResult<T> result;

    public SimulateGameCommand(Board board, Journal<ActionMemento<?,?>> journal,
                               ForkJoinPool forkJoinPool, Color color, Action<?> action) {

        this(new SimulationGame(board, journal, forkJoinPool, color, action));
    }

    private SimulateGameCommand(SimulationGame game) {
        super(LOGGER);
        this.game = game;
    }

    public void setSimulationEvaluator(SimulationEvaluator<T> evaluator) {
        this.evaluator = evaluator;
    }

    public ActionSimulationResult<T> getSimulationResult() {
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
        this.result = new ActionSimulationResult<>(game.getBoard(), game.getJournal(),
                game.getAction(), game.getColor(), value
        );
    }
}