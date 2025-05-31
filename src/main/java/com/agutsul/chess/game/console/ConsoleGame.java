package com.agutsul.chess.game.console;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.ai.SimulationActionInputObserver;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;

public final class ConsoleGame
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(ConsoleGame.class);

    private static final long TEN_MINUTES = 10 * 60 * 1000; // milliseconds

    public ConsoleGame(Player whitePlayer, Player blackPlayer) {
        this(whitePlayer, blackPlayer, new StandardBoard(), System.in);
    }

    ConsoleGame(Player whitePlayer, Player blackPlayer, Board board, InputStream inputStream) {
        super(LOGGER, whitePlayer, blackPlayer, board,
                new JournalImpl(), new GameContext(new ForkJoinPool(), TEN_MINUTES)
        );

        registerConsoleInputObserver(whitePlayer, inputStream);
        // uncomment to manually enter player actions
        //registerConsoleInputObserver(blackPlayer, inputStream);

        // uncomment to play against computer selecting actions randomly ( good for quick tests )
        //((Observable) board).addObserver(new RandomActionInputObserver(blackPlayer, this));

        // uncomment to play against computer
        ((Observable) board).addObserver(new SimulationActionInputObserver(blackPlayer, this));

        addObserver(new ConsoleGameOutputObserver(this));
    }

    private void registerConsoleInputObserver(Player player, InputStream inputStream) {
        ((Observable) getBoard()).addObserver(
                new ConsolePlayerInputObserver(player, this, inputStream)
        );
    }
}