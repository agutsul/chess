package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.rule.winner.WinnerEvaluator.Type;

public class GameWinnerEvent
        extends AbstractGameEvent {

    private final Type type;

    public GameWinnerEvent(Game game, Type type) {
        super(game);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}