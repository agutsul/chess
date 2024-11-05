package com.agutsul.chess.board;

import com.agutsul.chess.event.Observable;

public abstract class AbstractBoard
        implements Board, Observable {

    @Override
    public String toString() {
        return BoardFormatter.format(this);
    }
}