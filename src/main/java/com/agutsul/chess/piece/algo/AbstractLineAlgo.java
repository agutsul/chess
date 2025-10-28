package com.agutsul.chess.piece.algo;

import java.util.Collection;

import com.agutsul.chess.board.Board;

abstract class AbstractLineAlgo<SOURCE,RESULT>
        implements Algo<SOURCE,Collection<RESULT>> {

    protected final Board board;

    protected AbstractLineAlgo(Board board) {
        this.board = board;
    }
}