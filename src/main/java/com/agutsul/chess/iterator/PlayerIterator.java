package com.agutsul.chess.iterator;

import java.util.Iterator;

import com.agutsul.chess.player.Player;

public interface PlayerIterator
        extends Iterator<Player> {

    boolean hasPrevious();
    Player previous();
}