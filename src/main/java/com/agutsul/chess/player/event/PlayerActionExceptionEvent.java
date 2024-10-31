package com.agutsul.chess.player.event;

public class PlayerActionExceptionEvent
        extends AbstractExceptionEvent {

    public PlayerActionExceptionEvent(String message) {
        super(message);
    }
}