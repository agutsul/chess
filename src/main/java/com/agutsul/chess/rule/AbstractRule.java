package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.board.Board;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class AbstractRule<SOURCE,RESULT>
        implements Rule<SOURCE,Collection<RESULT>> {

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    protected final Board board;

    public AbstractRule(Board board) {
        this.board = board;
    }
}