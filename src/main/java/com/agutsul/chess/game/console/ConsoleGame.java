package com.agutsul.chess.game.console;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.player.Player;

public final class ConsoleGame
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(ConsoleGame.class);

    public ConsoleGame(Player whitePlayer, Player blackPlayer) {
        this(whitePlayer, blackPlayer, new StandardBoard());
    }

    ConsoleGame(Player whitePlayer, Player blackPlayer, Board board) {
        super(LOGGER, whitePlayer, blackPlayer, board);

        ((Observable) board).addObserver(new ConsolePlayerInputObserver(whitePlayer, this));
        ((Observable) board).addObserver(new ConsolePlayerInputObserver(blackPlayer, this));

        addObserver(new ConsoleGameOutputObserver(this));
    }
}