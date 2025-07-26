package com.agutsul.chess.game.console;

import static com.agutsul.chess.piece.Piece.isQueen;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

@ExtendWith(MockitoExtension.class)
public class ConsolePlayerInputObserverTest {

    private static final Player PLAYER = new UserPlayer("white_player", Colors.WHITE);

    @Mock
    GameContext context;

    @Mock
    AbstractPlayableGame game;

    @Mock
    Board board;

    @Test
    void testProcessRequestPlayerActionEvent() throws IOException {
        doNothing()
            .when(game).notifyObservers(any());

        when(context.getActionTimeout())
            .thenReturn(Optional.empty());

        when(game.getContext())
            .thenReturn(context);

        var exitCommand = String.format("exit%s", lineSeparator());
        try (var inputStream = new ByteArrayInputStream(exitCommand.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(PLAYER, game, inputStream);
            playerInputObserver.observe(new RequestPlayerActionEvent(PLAYER));
        }

        verify(game, times(2)).notifyObservers(any());
    }

    @Test
    void testProcessRequestPromotionPieceTypeEvent() throws IOException {
        doNothing()
            .when(game).notifyObservers(any());

        when(context.getActionTimeout())
            .thenReturn(Optional.empty());

        when(game.getContext())
            .thenReturn(context);

        var observer = new AbstractEventObserver<PromotionPieceTypeEvent>() {

            @Override
            protected void process(PromotionPieceTypeEvent event) {
                assertTrue(isQueen(event.getPieceType()));
            }
        };

        var promotionTypeCommand = String.format("q%s", lineSeparator());
        try (var inputStream = new ByteArrayInputStream(promotionTypeCommand.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(PLAYER, game, inputStream);
            playerInputObserver.observe(new RequestPromotionPieceTypeEvent(Colors.WHITE, observer));
        }

        verify(game, times(1)).notifyObservers(any());
    }

    @Test
    void testGetActionCommandValidCommandWithoutTimeout() throws IOException {
        var command = String.format("e2 e4%s", lineSeparator());
        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(PLAYER, game, inputStream);
            assertEquals("e2 e4", playerInputObserver.getActionCommand(Optional.empty()));
        }
    }

    @Test
    void testGetActionCommandValidCommandWithTimeout() throws IOException {
        var timeout = 1000L;

        var command = String.format("e2 e4%s", lineSeparator());
        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(PLAYER, game, inputStream);
            assertEquals("e2 e4", playerInputObserver.getActionCommand(Optional.of(timeout)));
        }
    }

    @Test
    void testGetActionCommandWithWinCommand() throws IOException {
        var command = String.format("win%s", lineSeparator());
        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(PLAYER, game, inputStream);
            var thrown = assertThrows(
                    IllegalActionException.class,
                    () -> playerInputObserver.getActionCommand(Optional.empty())
            );

            assertEquals("Unsupported command: 'win'", thrown.getMessage());
        }
    }

    @Test
    void testGetPromotionPieceTypeWithoutTimeout() throws IOException {
        var command = String.format("q%s", lineSeparator());
        try (var inputStream = new ByteArrayInputStream(command.getBytes())) {
            var playerInputObserver = new ConsolePlayerInputObserver(PLAYER, game, inputStream);
            assertEquals("Q", playerInputObserver.getPromotionPieceType(Optional.empty()));
        }
    }

    @Test
    void testGetPromotionPieceTypeWithTimeout() throws IOException {
        var timeout = Optional.of(10 * 60 * 1000L);

        var moveCommand = String.format("e7 e8%s", lineSeparator());
        try (var inputStream = new ByteArrayInputStream(moveCommand.getBytes())) {
            var playerInputObserver = new TestConsolePlayerInputObserver(PLAYER, game, inputStream);
            assertEquals("e7 e8", playerInputObserver.getActionCommand(timeout));

            var promotionTypeCommand = String.format("q%s", lineSeparator());
            try (var inputStream2 = new ByteArrayInputStream(promotionTypeCommand.getBytes())) {
                playerInputObserver.setInputStream(inputStream2);
                assertEquals("Q", playerInputObserver.getPromotionPieceType(timeout));
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