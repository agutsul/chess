package com.agutsul.chess.antlr;

import java.util.List;

import com.agutsul.chess.game.Game;

public interface AntlrGameListener<T extends Game> {
    List<T> getGames();
}
