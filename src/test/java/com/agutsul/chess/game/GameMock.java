package com.agutsul.chess.game;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.game.observer.GameExceptionObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.mock.PlayerActionOberverMock;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.board.BoardStateEvaluator;

public class GameMock
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(GameMock.class);

    public GameMock(Player whitePlayer, Player blackPlayer, Board board) {
        super(LOGGER, whitePlayer, blackPlayer, board, new JournalImpl());
    }

    public GameMock(Player whitePlayer, Player blackPlayer,
                    Board board, Journal<ActionMemento<?,?>> journal) {

        super(LOGGER, whitePlayer, blackPlayer, board, journal);
    }

    public GameMock(Player whitePlayer, Player blackPlayer,
                    Board board, Journal<ActionMemento<?,?>> journal,
                    ForkJoinPool forkJoinPool) {

        super(LOGGER, whitePlayer, blackPlayer, board, journal, new GameContext(forkJoinPool));
    }

    public GameMock(Player whitePlayer, Player blackPlayer,
                    Board board, Journal<ActionMemento<?,?>> journal,
                    BoardStateEvaluator<BoardState> boardStateEvaluator) {

        super(LOGGER, whitePlayer, blackPlayer, board, journal, boardStateEvaluator, new GameContext());
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Override
    protected void initObservers() {
        addObserver(new PlayerActionOberverMock(this));
        addObserver(new ActionEventObserver());
        addObserver(new GameExceptionObserver());
    }
}