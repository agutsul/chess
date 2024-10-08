package com.agutsul.chess.exception;

public class IllegalActionException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalActionException(String message) {
        super(message);
    }

    public IllegalActionException(Throwable cause) {
        super(cause);
    }

    public IllegalActionException(String message, Throwable cause) {
        super(message, cause);
    }
}