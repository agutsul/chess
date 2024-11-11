package com.agutsul.chess.board.event;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;

public class ClearPieceDataEvent
        implements Event {

    private final Color color;

    public ClearPieceDataEvent(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}