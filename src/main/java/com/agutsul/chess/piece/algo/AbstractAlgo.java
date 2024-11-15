package com.agutsul.chess.piece.algo;

import java.util.Collection;

import com.agutsul.chess.board.Board;

public abstract class AbstractAlgo<SOURCE,RESULT>
        implements Algo<SOURCE,Collection<RESULT>> {

    protected final Board board;

    protected AbstractAlgo(Board board) {
        this.board = board;
    }
}