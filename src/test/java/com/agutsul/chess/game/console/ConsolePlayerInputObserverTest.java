package com.agutsul.chess.game.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class ConsolePlayerInputObserverTest {

    @Test
    void testGetActionCommandValidCommandWithoutTimeout() throws IOException {
        var command = String.format("e2 e4%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        var game = mock(Game.class);
        when(game.getActionTimeout())
            .thenReturn(null);

        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(player, game, inputStream);
            assertEquals("e2 e4", playerInputObserver.getActionCommand());
        }
    }

    @Test
    void testGetActionCommandWithRuntimeException() throws IOException {
        var player = new UserPlayer("white_player", Colors.WHITE);

        var game = mock(Game.class);
        when(game.getActionTimeout())
            .thenReturn(null);

        var playerInputObserver = new ConsolePlayerInputObserver(player, game, null);
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> playerInputObserver.getActionCommand()
        );

        assertEquals("WHITE: 'white_player' Reading action from console failed", thrown.getMessage());
    }

    @Test
    void testGetActionCommandValidCommandWithTimeout() throws IOException {
        var command = String.format("e2 e4%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        var game = mock(Game.class);
        when(game.getActionTimeout())
            .thenReturn(1000L);

        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(player, game, inputStream);
            assertEquals("e2 e4", playerInputObserver.getActionCommand());
        }
    }

    @Test
    void testGetActionCommandWithBlankCommand() throws IOException {
        var command = String.format("%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        var game = mock(Game.class);
        when(game.getActionTimeout())
            .thenReturn(null);

        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(player, game, inputStream);
            var thrown = assertThrows(
                    IllegalActionException.class,
                    () -> playerInputObserver.getActionCommand()
            );

            assertEquals("Unable to process an empty line", thrown.getMessage());
        }
    }

    @Test
    void testGetActionCommandWithWinCommand() throws IOException {
        var command = String.format("win%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        var game = mock(Game.class);
        when(game.getActionTimeout())
            .thenReturn(null);

        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(player, game, inputStream);
            var thrown = assertThrows(
                    IllegalActionException.class,
                    () -> playerInputObserver.getActionCommand()
            );

            assertEquals("Unsupported command: 'win'", thrown.getMessage());
        }
    }

    @Test
    void testGetPromotionPieceTypeWithoutTimeout() throws IOException {
        var command = String.format("q%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        var game = mock(Game.class);
        when(game.getActionTimeout())
            .thenReturn(null);

        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(player, game, inputStream);
            assertEquals("Q", playerInputObserver.getPromotionPieceType());
        }
    }

    @Test
    void testGetPromotionPieceTypeWithBlankCommand() throws IOException {
        var command = String.format("%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        var game = mock(Game.class);
        when(game.getActionTimeout())
            .thenReturn(null);

        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(player, game, inputStream);

            var thrown = assertThrows(
                    IllegalActionException.class,
                    () -> playerInputObserver.getPromotionPieceType()
            );

            assertEquals("Unable to process an empty line", thrown.getMessage());
        }
    }

    @Test
    void testGetPromotionPieceTypeWithTimeout() throws IOException {
        var moveCommand = String.format("e7 e8%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        var game = mock(Game.class);
        when(game.getActionTimeout())
            .thenReturn(10 * 60 * 1000L);

        try (var inputStream = new ByteArrayInputStream(moveCommand.getBytes())) {
            var playerInputObserver = new TestConsolePlayerInputObserver(player, game, inputStream);
            assertEquals("e7 e8", playerInputObserver.getActionCommand());

            var promotionTypeCommand = String.format("q%s", System.lineSeparator());
            try (var inputStream2 = new ByteArrayInputStream(promotionTypeCommand.getBytes())) {
                playerInputObserver.setInputStream(inputStream2);
                assertEquals("Q", playerInputObserver.getPromotionPieceType());
            }
        }
    }

    private static final class TestConsolePlayerInputObserver
            extends ConsolePlayerInputObserver {

        public TestConsolePlayerInputObserver(Player player, Game game, InputStream inputStream) {
            super(player, game, inputStream);
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }
}