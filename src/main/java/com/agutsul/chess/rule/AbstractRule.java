package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.board.Board;

public abstract class AbstractRule<SOURCE,RESULT>
        implements Rule<SOURCE,Collection<RESULT>> {

    protected final Board board;

    public AbstractRule(Board board) {
        this.board = board;
    }
}