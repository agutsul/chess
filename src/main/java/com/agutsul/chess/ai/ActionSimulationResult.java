package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

public final class ActionSimulationResult<VALUE extends Comparable<VALUE>>
        implements TaskResult<Action<?>,VALUE> {

    private final Action<?> action;
    private final Color color;
    private final Board board;
    private final Journal<ActionMemento<?,?>> journal;
    private VALUE value;

    private TaskResult<Action<?>,VALUE> opponentResult;

    public ActionSimulationResult(Board board, Journal<ActionMemento<?,?>> journal,
                                  Action<?> action, Color color, VALUE value) {
        this.board = board;
        this.journal = journal;
        this.action = action;
        this.color = color;
        this.value = value;
    }

    @Override
    public Action<?> getAction() {
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
    public VALUE getValue() {
        return this.value;
    }

    public void setValue(VALUE value) {
        this.value = value;
    }

    @Override
    public TaskResult<Action<?>,VALUE> getOpponentResult() {
        return this.opponentResult;
    }

    public void setOpponentResult(TaskResult<Action<?>,VALUE> result) {
        this.opponentResult = result;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return String.format("%s: %s: %s",
                getColor(), String.valueOf(getAction()), String.valueOf(getValue())
        );
    }
}