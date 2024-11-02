package com.agutsul.chess;

import com.agutsul.chess.console.ConsoleGame;
import com.agutsul.chess.player.UserPlayer;

public class Application {

    public static void main(String[] args) {
        var game = new ConsoleGame(
                new UserPlayer("player1", Colors.WHITE),
                new UserPlayer("player2", Colors.BLACK)
        );

        game.run();
    }
}