package com.agutsul.chess.ai;

import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

public interface TaskResult<ACTION extends Action<?>,VALUE extends Comparable<VALUE>>
        extends Valuable<VALUE> {

    Color getColor();
    ACTION getAction();
    Board getBoard();
    Journal<ActionMemento<?,?>> getJournal();
    TaskResult<ACTION,VALUE> getOpponentResult();
}