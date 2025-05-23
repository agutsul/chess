package com.agutsul.chess.game.console;

import static org.apache.commons.io.input.CloseShieldInputStream.wrap;

import java.io.InputStream;

import com.agutsul.chess.player.Player;

abstract class AbstractConsoleInputReader
        implements ConsoleInputReader {

    protected final Player player;
    protected final InputStream inputStream;

    AbstractConsoleInputReader(Player player, InputStream inputStream) {
        this.player = player;
        this.inputStream = wrap(inputStream);
    }
}