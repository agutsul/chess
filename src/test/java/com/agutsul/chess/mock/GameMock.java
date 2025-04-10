package com.agutsul.chess.mock;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;

public class GameMock
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(GameMock.class);

    public GameMock(Player whitePlayer, Player blackPlayer, Board board) {
        this(whitePlayer, blackPlayer, board, new JournalImpl(), ForkJoinPool.commonPool());
    }

    public GameMock(Player whitePlayer, Player blackPlayer, Board board,
                    Journal<ActionMemento<?,?>> journal) {

        this(whitePlayer, blackPlayer, board, journal, ForkJoinPool.commonPool());
    }

    public GameMock(Player whitePlayer, Player blackPlayer, Board board,
                    Journal<ActionMemento<?,?>> journal, ForkJoinPool forkJoinPool) {

        this(whitePlayer, blackPlayer, board, journal, forkJoinPool,
                new BoardStateEvaluatorImpl(board, journal, forkJoinPool)
        );
    }

    public GameMock(Player whitePlayer, Player blackPlayer, Board board,
                    Journal<ActionMemento<?,?>> journal,
                    BoardStateEvaluator<BoardState> boardStateEvaluator) {

        super(LOGGER, whitePlayer, blackPlayer, board, journal,
                ForkJoinPool.commonPool(), boardStateEvaluator);
    }

    public GameMock(Player whitePlayer, Player blackPlayer, Board board,
                    Journal<ActionMemento<?,?>> journal, ForkJoinPool forkJoinPool,
                    BoardStateEvaluator<BoardState> boardStateEvaluator) {

        super(LOGGER, whitePlayer, blackPlayer, board, journal, forkJoinPool, boardStateEvaluator);
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}