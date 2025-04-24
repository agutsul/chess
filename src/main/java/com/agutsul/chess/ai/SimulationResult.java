package com.agutsul.chess.ai;

import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.journal.Journal;

public interface SimulationResult<A extends Activity<?,?>,V extends Comparable<V>>
        extends Valuable<V> {

    A getSimulated();
    Board getBoard();
    Journal<ActionMemento<?,?>> getJournal();
}