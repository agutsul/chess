package com.agutsul.chess.ai;

import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.journal.Journal;

public interface SimulationResult<A extends Action<?>,V extends Comparable<V>>
        extends Valuable<V> {

    A getAction();
    Board getBoard();
    Journal<ActionMemento<?,?>> getJournal();
}