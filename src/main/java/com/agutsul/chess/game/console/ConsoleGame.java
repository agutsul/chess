package com.agutsul.chess.game.console;

import java.io.InputStream;
import java.util.concurrent.ForkJoinPool;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractGameProxy;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.PlayableGameBuilder;
import com.agutsul.chess.game.ai.SimulationActionInputObserver;
import com.agutsul.chess.player.Player;

public final class ConsoleGame<T extends Game & Observable>
        extends AbstractGameProxy<T> {

    private static final long TEN_MINUTES = 10 * 60 * 1000; // milliseconds

    public ConsoleGame(Player whitePlayer, Player blackPlayer) {
        super(createGame(whitePlayer, blackPlayer,
                new StandardBoard(), System.in, TEN_MINUTES, null
        ));

        addObserver(new ConsoleGameOutputObserver(game));
    }

    public ConsoleGame(Player whitePlayer, Player blackPlayer, Long actionTimeout, Long gameTimeout) {
        super(createGame(whitePlayer, blackPlayer,
                new StandardBoard(), System.in, actionTimeout, gameTimeout
        ));

        addObserver(new ConsoleGameOutputObserver(game));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Game & Observable> T createGame(Player whitePlayer, Player blackPlayer,
                                                              Board board, InputStream inputStream,
                                                              Long actionTimeout, Long gameTimeout) {

        var game = new PlayableGameBuilder<>(whitePlayer, blackPlayer)
                .withBoard(board)
                .withContext(new GameContext(new ForkJoinPool(), actionTimeout, gameTimeout))
                .build();

        var observableBoard = (Observable) board;

        observableBoard.addObserver(new ConsolePlayerInputObserver(whitePlayer, game, inputStream));
        // uncomment to manually enter player actions
        //observableBoard.addObserver(new ConsolePlayerInputObserver(blackPlayer, game, inputStream));

        // uncomment to play against computer selecting actions randomly ( good for quick tests )
        //observableBoard.addObserver(new RandomActionInputObserver(blackPlayer, game));

        // uncomment to play against computer
        observableBoard.addObserver(new SimulationActionInputObserver(blackPlayer, game));

        return (T) game;
    }
}