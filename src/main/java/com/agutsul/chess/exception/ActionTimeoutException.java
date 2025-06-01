package com.agutsul.chess.exception;

public class ActionTimeoutException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ActionTimeoutException(String message) {
        super(message);
    }
}