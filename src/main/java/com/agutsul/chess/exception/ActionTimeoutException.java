package com.agutsul.chess.exception;

import com.agutsul.chess.player.Player;

public final class ActionTimeoutException
        extends AbstractTimeoutException {

    private static final long serialVersionUID = 1L;

    public ActionTimeoutException(Player player) {
        super(player, Type.ACTION);
    }
}