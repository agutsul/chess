package com.agutsul.chess.game.console;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractGameProxy;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.GameImpl;
import com.agutsul.chess.game.TimeoutGame;
import com.agutsul.chess.game.ai.SimulationActionInputObserver;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;

public final class ConsoleGame
        extends AbstractGameProxy {

    private static final Logger LOGGER = getLogger(ConsoleGame.class);

    private static final long TEN_MINUTES = 10 * 60 * 1000; // milliseconds

    public ConsoleGame(Player whitePlayer, Player blackPlayer) {
        this(whitePlayer, blackPlayer, new StandardBoard(), System.in);
    }

    public ConsoleGame(Player whitePlayer, Player blackPlayer, Long actionTimeout, Long gameTimeout) {
        super(createGame(whitePlayer, blackPlayer, new StandardBoard(),
                System.in, actionTimeout, gameTimeout
        ));
    }

    ConsoleGame(Player whitePlayer, Player blackPlayer, Board board, InputStream inputStream) {
        super(createGame(whitePlayer, blackPlayer, board, inputStream, TEN_MINUTES, null));
    }

    private static Game createGame(Player whitePlayer, Player blackPlayer,
                                   Board board, InputStream inputStream,
                                   Long actionTimeout, Long gameTimeout) {

        var context = new GameContext(new ForkJoinPool(), actionTimeout, gameTimeout);
        var game = new GameImpl(LOGGER, whitePlayer, blackPlayer, board, new JournalImpl(), context);

        var observableBoard = (Observable) board;

        observableBoard.addObserver(new ConsolePlayerInputObserver(whitePlayer, game, inputStream));
        // uncomment to manually enter player actions
        //observableBoard.addObserver(new ConsolePlayerInputObserver(blackPlayer, game, inputStream));

        // uncomment to play against computer selecting actions randomly ( good for quick tests )
        //observableBoard.addObserver(new RandomActionInputObserver(blackPlayer, game));

        // uncomment to play against computer
        observableBoard.addObserver(new SimulationActionInputObserver(blackPlayer, game));

        game.addObserver(new ConsoleGameOutputObserver(game));

        return context.getGameTimeout() != null
                ? new TimeoutGame(game, context.getGameTimeout())
                : game;
    }

}