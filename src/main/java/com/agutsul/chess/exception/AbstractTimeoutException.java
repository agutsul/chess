package com.agutsul.chess.exception;

import com.agutsul.chess.player.Player;

public abstract class AbstractTimeoutException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE_FORMAT = "%s: '%s' entering %s timeout";

    protected AbstractTimeoutException(String message) {
        super(message);
    }

    protected AbstractTimeoutException(Player player, String type) {
        this(String.format(MESSAGE_FORMAT, player.getColor(), player, type));
    }
}
