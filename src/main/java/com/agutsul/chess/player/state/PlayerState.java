package com.agutsul.chess.player.state;

import com.agutsul.chess.player.Player;
import com.agutsul.chess.state.State;

public interface PlayerState
        extends State<Player> {

    enum Type {
        ACTIVE,
        LOCKED
    }

    Type getType();
}