package com.agutsul.chess.exception;

import com.agutsul.chess.player.Player;

public final class GameTimeoutException
        extends AbstractTimeoutException {

    private static final long serialVersionUID = 1L;

    public GameTimeoutException(Player player) {
        super(player, Type.GAME);
    }

    public GameTimeoutException(Player player, String message) {
        super(player, Type.GAME, message);
    }
}