package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.board.Board;

public abstract class AbstractRule<SOURCE,RESULT,TYPE extends Enum<TYPE>>
        implements Rule<SOURCE,Collection<RESULT>> {

    protected final Board board;
    private final TYPE type;

    public AbstractRule(Board board, TYPE type) {
        this.board = board;
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }
}