package com.agutsul.chess.player.state;

import com.agutsul.chess.player.Player;

public class LockedPlayerState extends AbstractPlayerState {

    public LockedPlayerState() {
        super(Type.LOCKED);
    }

    @Override
    public void play(Player player) {
        // do nothing
    }
}