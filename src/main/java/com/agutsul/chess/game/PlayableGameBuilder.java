package com.agutsul.chess.game;

import static java.util.Objects.isNull;

import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.observer.GameTimeoutTerminationObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;
import com.agutsul.chess.timeout.CompositeTimeout;

public final class PlayableGameBuilder<GAME extends Game & Playable>
        implements GameBuilder<GAME> {

    private final Player whitePlayer;
    private final Player blackPlayer;

    private Color color;

    private Board board;
    private Journal<ActionMemento<?,?>> journal;
    private GameContext context;
    private BoardStateEvaluator<BoardState> boardStateEvaluator;

    public PlayableGameBuilder(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public PlayableGameBuilder<GAME> withActiveColor(Color color) {
        this.color = color;
        return this;
    }

    public PlayableGameBuilder<GAME> withBoard(Board board) {
        this.board = board;
        return this;
    }

    public PlayableGameBuilder<GAME> withJournal(Journal<ActionMemento<?,?>> journal) {
        this.journal = journal;
        return this;
    }

    public PlayableGameBuilder<GAME> withContext(GameContext context) {
        this.context = context;
        return this;
    }

    public PlayableGameBuilder<GAME> withBoardStateEvaluator(BoardStateEvaluator<BoardState> evaluator) {
        this.boardStateEvaluator = evaluator;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GAME build() {
        var board   = this.board   != null ? this.board   : new StandardBoard();
        var journal = this.journal != null ? this.journal : new JournalImpl();
        var context = this.context != null ? this.context : new GameContext();

        var boardStateEvaluator = this.boardStateEvaluator != null
                ? this.boardStateEvaluator
                : new BoardStateEvaluatorImpl(board, journal, context.getForkJoinPool())
        ;

        var game = new GameImpl(this.whitePlayer, this.blackPlayer,
                board, journal, boardStateEvaluator, context
        );

        if (!isNull(this.color)) {
            game.setCurrentPlayer(game.getPlayer(this.color));
            // re-evaluate board state
            board.setState(game.evaluateBoardState(game.getCurrentPlayer()));
        }

        var compositeGame = Stream.of(context.getTimeout())
                .flatMap(Optional::stream)
                .filter(timeout -> timeout instanceof CompositeTimeout)
                .map(timeout  -> new CompositeGame<>(game, (CompositeTimeout) timeout))
                .peek(comGame -> comGame.addObserver(new GameTimeoutTerminationObserver()))
                .map(comGame -> (GAME) comGame)
                .findFirst();

        if (compositeGame.isPresent()) {
            return compositeGame.get();
        }

        var playableGame = Stream.of(context.getGameTimeout())
                .flatMap(Optional::stream)
                .map(timeoutMillis -> new TimeoutGame<>(game, timeoutMillis))
                .peek(timeoutGame -> timeoutGame.addObserver(new GameTimeoutTerminationObserver()))
                .map(timeoutGame -> (GAME) timeoutGame)
                .findFirst()
                .orElse((GAME) game);

        return playableGame;
    }
}