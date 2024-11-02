package com.agutsul.chess.game;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.CancelMoveAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.action.event.ActionCancellingEvent;
import com.agutsul.chess.action.event.ActionExecutionEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.mock.GameMock;
import com.agutsul.chess.mock.GameOutputObserverMock;
import com.agutsul.chess.mock.PlayerInputObserverMock;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;

@ExtendWith(MockitoExtension.class)
public class GameTest {

    @Test
    void testPlayerAskedAction() {
        var board = mock(Board.class);

        when(board.isChecked(any()))
            .thenReturn(false);
        when(board.isStaleMated(any()))
            .thenReturn(true);

        var whitePlayer = mock(Player.class);
        var blackPlayer = mock(Player.class);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        game.run();

        verify(whitePlayer, times(1)).play();
        verify(blackPlayer, never()).play();
    }

    @Test
    void testGetWinner() {
        var board = mock(Board.class);
        when(board.getState())
            .thenReturn(new CheckMatedBoardState(board, Colors.WHITE));

        var whitePlayer = new UserPlayer("test1", Colors.WHITE);
        var blackPlayer = new UserPlayer("test2", Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        var winner = game.getWinner();

        assertTrue(winner.isPresent());
        assertEquals(whitePlayer, winner.get());
    }

    @Test
    void testPositiveGameFlow() {
        var board = spy(new StandardBoard());
        doCallRealMethod().when(board).notifyObservers(any());

        when(board.isChecked(any()))
            .thenReturn(false);
        when(board.isStaleMated(any()))
            .thenAnswer(inv -> {
                // allow white and black moves and breaks on the second white move
                return Colors.WHITE.equals(inv.getArgument(0, Color.class));
            });

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod().when(blackPlayer).play();

        var game = new GameMock(whitePlayer, blackPlayer, board);

        board.addObserver(new PlayerInputObserverInteratorMock(whitePlayer, game, "e2 e4"));
        board.addObserver(new PlayerInputObserverInteratorMock(blackPlayer, game, "e7 e5"));

        Map<Class<? extends Event>, BiConsumer<Game,Event>> assertionMap = new HashMap<>();
        assertionMap.put(GameStartedEvent.class, (gm, evt) -> {
            assertEquals(gm, game);
            assertTrue(evt instanceof GameStartedEvent);
            assertEquals(gm, ((GameStartedEvent) evt).getGame());
        });
        assertionMap.put(GameOverEvent.class, (gm, evt) -> {
            assertEquals(gm, game);
            assertTrue(evt instanceof GameOverEvent);
            assertEquals(gm, ((GameOverEvent) evt).getGame());
        });
        assertionMap.put(ActionPerformedEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof ActionPerformedEvent);
            assertNotNull(((ActionPerformedEvent) evt).getActionMemento());
        });
        assertionMap.put(ActionExecutionEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof ActionExecutionEvent);

            var action = ((ActionExecutionEvent) evt).getAction();
            assertTrue(action instanceof PieceMoveAction);
        });

        game.addObserver(new GameOutputObserverMock(game, assertionMap));
        game.run();

        assertEquals(2, game.getJournal().size());
        assertTrue(game.getWinner().isEmpty());

        verify(whitePlayer, times(1)).play();
        verify(blackPlayer, times(1)).play();
    }

    @Test
    void testPositiveGameFlowWithWhiteUndo() {
        var board = spy(new StandardBoard());
        doCallRealMethod().when(board).notifyObservers(any());

        var wPlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(wPlayer).play();

        var bPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod().when(bPlayer).play();

        var game = new GameMock(wPlayer, bPlayer, board);

        var wPlayerObserver = new PlayerInputObserverInteratorMock(wPlayer, game, "e2 e4", "undo", "e2 e4");
        var bPlayerObserver = new PlayerInputObserverInteratorMock(bPlayer, game, "e7 e5", "e7 e5");

        board.addObserver(wPlayerObserver);
        board.addObserver(bPlayerObserver);

        when(board.isChecked(any()))
            .thenReturn(false);
        when(board.isStaleMated(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var isWhiteMoveAvailable = wPlayerObserver.getIterator().hasNext();

                return Colors.WHITE.equals(color) && !isWhiteMoveAvailable;
            });

        Map<Class<? extends Event>, BiConsumer<Game,Event>> assertionMap = new HashMap<>();
        assertionMap.put(ActionCancellingEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof ActionCancellingEvent);

            var action = ((ActionCancellingEvent) evt).getAction();
            assertTrue(action instanceof CancelMoveAction);
        });
        assertionMap.put(ActionCancelledEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof ActionCancelledEvent);
        });

        game.addObserver(new GameOutputObserverMock(game, assertionMap));
        game.run();

        assertEquals(2, game.getJournal().size());
        assertTrue(game.getWinner().isEmpty());

        verify(wPlayer, times(2)).play();
        verify(bPlayer, times(2)).play();
    }

    @Test
    void testNegativeGameFlowWithWhiteUndo() {
        var board = spy(new StandardBoard());
        doCallRealMethod().when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod().when(blackPlayer).play();

        var game = new GameMock(whitePlayer, blackPlayer, board);

        var playerInputObserver = new PlayerInputObserverInteratorMock(
                whitePlayer, game, "undo", "e2 e4"
        );

        board.addObserver(playerInputObserver);
        board.addObserver(new PlayerInputObserverInteratorMock(blackPlayer, game, "e7 ", "e7 e5"));

        when(board.isChecked(any()))
            .thenReturn(false);
        when(board.isStaleMated(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                // allow white and black moves and breaks on the second white move
                return Colors.WHITE.equals(color)
                        && !playerInputObserver.getIterator().hasNext();
            });

        Map<Class<? extends Event>, BiConsumer<Game,Event>> assertionMap = new HashMap<>();
        assertionMap.put(PlayerCancelActionExceptionEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof PlayerCancelActionExceptionEvent);

            var message = ((PlayerCancelActionExceptionEvent) evt).getMessage();
            assertEquals("No action to cancel", message);
        });
        assertionMap.put(PlayerActionExceptionEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof PlayerActionExceptionEvent);

            var message = ((PlayerActionExceptionEvent) evt).getMessage();
            assertEquals("Invalid action format: 'e7 '", message);
        });

        game.addObserver(new GameOutputObserverMock(game, assertionMap));
        game.run();

        assertEquals(2, game.getJournal().size());
        assertTrue(game.getWinner().isEmpty());

        verify(whitePlayer, times(1)).play();
        verify(blackPlayer, times(1)).play();
    }

    private static class PlayerInputObserverInteratorMock
            extends PlayerInputObserverMock {

        private final Iterator<String> actionInterator;

        PlayerInputObserverInteratorMock(Player player, Game game, String... actions) {
            super(player, game);
            this.actionInterator = asList(actions).iterator();
        }

        public Iterator<String> getIterator() {
            return actionInterator;
        }

        @Override
        protected String getActionCommand() {
            return actionInterator.hasNext() ? actionInterator.next() : null;
        }
    }
}