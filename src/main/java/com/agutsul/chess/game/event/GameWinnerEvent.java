package com.agutsul.chess.game.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.rule.winner.WinnerEvaluator.Type;

public class GameWinnerEvent
        implements Event {

    private final Type type;

    public GameWinnerEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}