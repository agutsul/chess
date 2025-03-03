package com.agutsul.chess.antlr;

import java.util.List;

import com.agutsul.chess.game.Game;

public interface AntlrParser<T extends Game,S> {
    List<T> parse(S string);
}