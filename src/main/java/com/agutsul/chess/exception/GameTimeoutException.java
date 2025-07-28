package com.agutsul.chess.exception;

import com.agutsul.chess.player.Player;

public final class GameTimeoutException
        extends AbstractTimeoutException {

    private static final long serialVersionUID = 1L;

    private static final String GAME_MESSAGE = "game";

    public GameTimeoutException(String message) {
        super(message);
    }

    public GameTimeoutException(Player player) {
        super(player, GAME_MESSAGE);
    }
}