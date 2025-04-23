package com.agutsul.chess.ai;

import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.journal.Journal;

public interface SimulationResult<T,V extends Comparable<V>>
        extends Valuable<V> {

    Board getBoard();
    Journal<ActionMemento<?,?>> getJournal();
}