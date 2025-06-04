package com.agutsul.chess.game;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.board.BoardStateEvaluator;

final class GameImpl extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(GameImpl.class);

    GameImpl(Player whitePlayer, Player blackPlayer,
             Board board, Journal<ActionMemento<?,?>> journal,
             BoardStateEvaluator<BoardState> boardStateEvaluator,
             GameContext context) {

        super(LOGGER, whitePlayer, blackPlayer, board, journal, boardStateEvaluator, context);
    }
}