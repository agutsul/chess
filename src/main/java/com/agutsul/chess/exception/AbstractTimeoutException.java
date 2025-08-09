package com.agutsul.chess.exception;

import com.agutsul.chess.player.Player;

public abstract class AbstractTimeoutException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE_FORMAT = "%s: '%s' entering %s timeout";

    protected enum Type {
        ACTION,
        GAME
    }

    private final Player player;
    private final Type type;

    protected AbstractTimeoutException(Player player, Type type) {
        this(player, type, String.format(MESSAGE_FORMAT,
                player.getColor(), player, type.name().toLowerCase()
        ));
    }

    protected AbstractTimeoutException(Player player, Type type, String message) {
        super(message);

        this.player = player;
        this.type = type;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Type getType() {
        return this.type;
    }
}