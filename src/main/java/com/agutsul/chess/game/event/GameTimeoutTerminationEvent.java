package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public class GameTimeoutTerminationEvent
        extends AbstractGameEvent
        implements GameTerminationEvent {

    private final Player player;

    public GameTimeoutTerminationEvent(Game game, Player player) {
        super(game);
        this.player = player;
    }

    @Override
    public Type getType() {
        return Type.TIMEOUT;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }
}