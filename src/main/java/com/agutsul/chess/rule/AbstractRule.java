package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.board.Board;

public abstract class AbstractRule<SOURCE extends Positionable,
                                   RESULT extends Activity<?>,
                                   TYPE extends Enum<TYPE> & Activity.Type>
        implements Rule<SOURCE,Collection<RESULT>> {

    protected final Board board;
    private final TYPE type;

    public AbstractRule(Board board, TYPE type) {
        this.board = board;
        this.type = type;
    }

    public final TYPE getType() {
        return type;
    }
}