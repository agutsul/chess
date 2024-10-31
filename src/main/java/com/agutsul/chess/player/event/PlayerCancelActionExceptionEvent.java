package com.agutsul.chess.player.event;

public class PlayerCancelActionExceptionEvent
        extends AbstractExceptionEvent {

    public PlayerCancelActionExceptionEvent(String message) {
        super(message);
    }
}