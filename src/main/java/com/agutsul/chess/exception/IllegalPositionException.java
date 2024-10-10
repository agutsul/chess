package com.agutsul.chess.exception;

public class IllegalPositionException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalPositionException(String s) {
        super(s);
    }

    public IllegalPositionException(Throwable cause) {
        super(cause);
    }

    public IllegalPositionException(String message, Throwable cause) {
        super(message, cause);
    }
}