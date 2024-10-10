package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;

public class PlayerActionExceptionEvent
    implements Event {

    private final String message;

    public PlayerActionExceptionEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}