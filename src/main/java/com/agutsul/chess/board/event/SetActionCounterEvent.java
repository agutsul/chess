package com.agutsul.chess.board.event;

import com.agutsul.chess.event.Event;

public final class SetActionCounterEvent
        implements Event {

    private final int counter;

    public SetActionCounterEvent(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }
}