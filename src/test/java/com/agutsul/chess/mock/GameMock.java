package com.agutsul.chess.mock;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;

public class GameMock
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(GameMock.class);

    public GameMock(Player whitePlayer, Player blackPlayer, Board board) {
        this(whitePlayer, blackPlayer, board, new JournalImpl<Memento>());
    }

    public GameMock(Player whitePlayer, Player blackPlayer,
            Board board, Journal<Memento> journal) {

        this(whitePlayer, blackPlayer,
                board, journal, new BoardStateEvaluatorImpl(board, journal));
    }

    public GameMock(Player whitePlayer, Player blackPlayer,
            Board board, Journal<Memento> journal,
            BoardStateEvaluator<BoardState> boardStateEvaluator) {

        super(LOGGER, whitePlayer, blackPlayer,
                board, journal, boardStateEvaluator);
    }
}