package com.agutsul.chess.console;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.player.Player;

public final class ConsoleGame extends AbstractGame {

    public ConsoleGame(Player whitePlayer, Player blackPlayer) {
        this(whitePlayer, blackPlayer, new StandardBoard());
    }

    ConsoleGame(Player whitePlayer, Player blackPlayer, Board board) {
        super(whitePlayer, blackPlayer, board);

        board.addObserver(new ConsoleRequestPlayerActionObserver(this));
        board.addObserver(new ConsoleRequestPromotionPieceTypeObserver());

        addObserver(new ConsoleGameObserver(this));
    }
}