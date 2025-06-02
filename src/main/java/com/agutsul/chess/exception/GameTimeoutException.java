package com.agutsul.chess.exception;

public class GameTimeoutException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GameTimeoutException(String message) {
        super(message);
    }
}