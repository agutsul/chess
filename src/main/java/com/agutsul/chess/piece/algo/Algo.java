package com.agutsul.chess.piece.algo;

public interface Algo<SOURCE,RESULT> {
    RESULT calculate(SOURCE source);
}