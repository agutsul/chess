package com.agutsul.chess.mock;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.player.Player;

public class GameMock
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(GameMock.class);

    public GameMock(Player whitePlayer, Player blackPlayer, Board board) {
        super(LOGGER, whitePlayer, blackPlayer, board);
    }
}