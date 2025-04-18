package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

public final class ActionSimulationResult
        implements SimulationResult<Action<?>> {

    private final Action<?> action;
    private final Color color;
    private final Board board;
    private final Journal<ActionMemento<?,?>> journal;
    private Integer value;

    private ActionSimulationResult opponentActionResult;

    public ActionSimulationResult(Board board, Journal<ActionMemento<?,?>> journal,
                                  Action<?> action, Color color, Integer value) {
        this.board = board;
        this.journal = journal;
        this.action = action;
        this.color = color;
        this.value = value;
    }

    public void setOpponentActionResult(ActionSimulationResult result) {
        this.opponentActionResult = result;

        if (result != null) {
            this.value += result.getValue();
        }
    }

    public ActionSimulationResult getOpponentActionResult() {
        return this.opponentActionResult;
    }

    public Action<?> getAction() {
        return action;
    }

    public Color getColor() {
        return color;
    }

    public Board getBoard() {
        return board;
    }

    public Journal<ActionMemento<?,?>> getJournal() {
        return journal;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}