package com.agutsul.chess.player.event;

public class PlayerDefeatActionExceptionEvent
        extends AbstractExceptionEvent {

    public PlayerDefeatActionExceptionEvent(String message) {
        super(message);
    }
}