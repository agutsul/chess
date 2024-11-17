package com.agutsul.chess.rule.board;

import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.journal.Journal;

abstract class AbstractJournalStateEvaluator
        extends AbstractBoardStateEvaluator {

    protected final Journal<ActionMemento<?,?>> journal;

    AbstractJournalStateEvaluator(Board board,
                                  Journal<ActionMemento<?,?>> journal) {
        super(board);
        this.journal = journal;
    }
}