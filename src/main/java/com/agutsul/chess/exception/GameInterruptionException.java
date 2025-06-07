package com.agutsul.chess.exception;

public class GameInterruptionException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GameInterruptionException(String message) {
        super(message);
    }
}
