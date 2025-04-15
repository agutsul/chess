package com.agutsul.chess.command;

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

public final class SimulateGameCommand
        extends AbstractCommand
        implements Closeable, Valuable {

    private static final Logger LOGGER = getLogger(SimulateGameCommand.class);

    private final SimulationGame game;

    private SimulationEvaluator evaluator;
    private int value;

    public SimulateGameCommand(Board board, Journal<ActionMemento<?,?>> journal,
                               ForkJoinPool forkJoinPool, Color color, Action<?> action) {

        this(new SimulationGame(color, board, journal, forkJoinPool, action));
    }

    private SimulateGameCommand(SimulationGame game) {
        super(LOGGER);
        this.game = game;
    }

    public void setSimulationEvaluator(SimulationEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public Game getGame() {
        return this.game;
    }

    @Override
    public int getValue() {
        return this.value;
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
        this.value = this.evaluator.evaluate(this.game);
    }
}