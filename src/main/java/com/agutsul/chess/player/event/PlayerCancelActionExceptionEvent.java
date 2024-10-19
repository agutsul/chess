package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;

public class PlayerCancelActionExceptionEvent
    implements Event {

    private final String message;

    public PlayerCancelActionExceptionEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}