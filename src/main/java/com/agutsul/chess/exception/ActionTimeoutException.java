package com.agutsul.chess.exception;

import com.agutsul.chess.player.Player;

public final class ActionTimeoutException
        extends AbstractTimeoutException {

    private static final long serialVersionUID = 1L;

    private static final String ACTION_MESSAGE = "action";

    public ActionTimeoutException(String message) {
        super(message);
    }

    public ActionTimeoutException(Player player) {
        super(player, ACTION_MESSAGE);
    }
}