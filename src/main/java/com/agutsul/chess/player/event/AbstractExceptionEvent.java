package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;

public abstract class AbstractExceptionEvent
        implements Event {

    private final String message;

    protected AbstractExceptionEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}