package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

public final class ActionSimulationResult<T extends Comparable<T>>
        implements SimulationResult<Action<?>,T> {

    private final Action<?> action;
    private final Color color;
    private final Board board;
    private final Journal<ActionMemento<?,?>> journal;
    private T value;

    private SimulationResult<Action<?>,T> opponentResult;

    public ActionSimulationResult(Board board, Journal<ActionMemento<?,?>> journal,
                                  Action<?> action, Color color, T value) {
        this.board = board;
        this.journal = journal;
        this.action = action;
        this.color = color;
        this.value = value;
    }

    @Override
    public Action<?> getSimulated() {
        return action;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public Journal<ActionMemento<?,?>> getJournal() {
        return journal;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public SimulationResult<Action<?>,T> getOpponentResult() {
        return this.opponentResult;
    }

    public void setOpponentResult(SimulationResult<Action<?>,T> result) {
        this.opponentResult = result;
    }

    public Color getColor() {
        return color;
    }
}