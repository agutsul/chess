package com.agutsul.chess.game;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.board.BoardStateEvaluator;

public final class GameImpl
        extends AbstractPlayableGame {

    public GameImpl(Logger logger, Player whitePlayer, Player blackPlayer, Board board) {
        super(logger, whitePlayer, blackPlayer, board);
    }

    public GameImpl(Logger logger, Player whitePlayer, Player blackPlayer,
                    Board board, Journal<ActionMemento<?,?>> journal) {

        super(logger, whitePlayer, blackPlayer, board, journal);
    }

    public GameImpl(Logger logger, Player whitePlayer, Player blackPlayer,
                    Board board, Journal<ActionMemento<?,?>> journal, GameContext context) {

        super(logger, whitePlayer, blackPlayer, board, journal, context);
    }

    public GameImpl(Logger logger, Player whitePlayer, Player blackPlayer,
                    Board board, Journal<ActionMemento<?,?>> journal,
                    BoardStateEvaluator<BoardState> boardStateEvaluator, GameContext context) {

        super(logger, whitePlayer, blackPlayer, board, journal, boardStateEvaluator, context);
    }
}