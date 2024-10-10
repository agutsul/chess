package com.agutsul.chess.exception;

public class IllegalBoardException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalBoardException(String message) {
        super(message);
    }

    public IllegalBoardException(Throwable cause) {
        super(cause);
    }

    public IllegalBoardException(String message, Throwable cause) {
        super(message, cause);
    }
}