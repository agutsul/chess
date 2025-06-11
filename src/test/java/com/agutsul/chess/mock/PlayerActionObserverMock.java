package com.agutsul.chess.mock;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.PlayerActionObserver;

public class PlayerActionObserverMock
        extends PlayerActionObserver {

    public PlayerActionObserverMock(Game game) {
        super(game);
    }

    @Override
    protected void requestPlayerAction(Player player) {
        // do nothing ( should be used inside tests only ).
        // prevent unbound continuous action re-ask
    }
}