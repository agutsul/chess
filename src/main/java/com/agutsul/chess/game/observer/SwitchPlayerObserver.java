package com.agutsul.chess.game.observer;

import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.SwitchPlayerEvent;

public class SwitchPlayerObserver
        extends AbstractEventObserver<SwitchPlayerEvent> {

    private final Game game;

    public SwitchPlayerObserver(Game game) {
        this.game = game;
    }

    @Override
    protected void process(SwitchPlayerEvent event) {
        game.getOpponentPlayer().idle();
        game.getCurrentPlayer().activate();
    }
}