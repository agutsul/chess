package com.agutsul.chess.exception;

public class UnknownPieceException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnknownPieceException(String message) {
        super(message);
    }

    public UnknownPieceException(Throwable cause) {
        super(cause);
    }

    public UnknownPieceException(String message, Throwable cause) {
        super(message, cause);
    }
}