package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Termination;
import com.agutsul.chess.player.Player;

public interface GameTerminationEvent
        extends Termination {

    enum Type {
        EXIT,
        DRAW,
        DEFEAT,
        WIN,
        TIMEOUT
    }

    Type getType();

    Player getPlayer();
}