package com.agutsul.chess.game.observer;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameMock;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class GameStartedObserverTest {

    @Test
    void testGameStartedEvent() {
        var whitePlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.WHITE);
        var blackPlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.BLACK);

        var game = new InitialGameMock(whitePlayer, blackPlayer, new StandardBoard());

        assertNull(game.getStartedAt());

        game.run();

        assertNotNull(game.getStartedAt());
    }

    private static final class InitialGameMock
            extends GameMock {

        InitialGameMock(Player whitePlayer, Player blackPlayer, Board board) {
            super(whitePlayer, blackPlayer, board);
        }

        @Override
        public void run() {
            notifyObservers(new GameStartedEvent(this));
        }
    }
}