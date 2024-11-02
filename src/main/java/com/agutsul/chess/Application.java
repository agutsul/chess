package com.agutsul.chess;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.console.ConsoleGame;
import com.agutsul.chess.player.UserPlayer;

public class Application
        implements Executable {

    public static void main(String[] args) {
        new Application().execute();
    }

    @Override
    public void execute() {
        var game = new ConsoleGame(
                new UserPlayer("player1", Colors.WHITE),
                new UserPlayer("player2", Colors.BLACK)
        );

        game.run();
    }
}