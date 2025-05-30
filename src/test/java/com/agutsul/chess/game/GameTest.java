package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDefeatBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedDrawBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.exitedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.agutsul.chess.activity.action.CancelMoveAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.event.ActionCancelledEvent;
import com.agutsul.chess.activity.action.event.ActionCancellingEvent;
import com.agutsul.chess.activity.action.event.ActionExecutionEvent;
import com.agutsul.chess.activity.action.event.ActionPerformedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.state.AgreedBoardState;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.board.state.ExitedBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.event.GameTerminationEvent;
import com.agutsul.chess.game.state.BlackWinGameState;
import com.agutsul.chess.game.state.DefaultGameState;
import com.agutsul.chess.game.state.DrawnGameState;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.game.state.WhiteWinGameState;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.mock.GameOutputObserverMock;
import com.agutsul.chess.mock.PlayerInputObserverMock;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;
import com.agutsul.chess.rule.board.BoardStateEvaluator;

@ExtendWith(MockitoExtension.class)
public class GameTest {

    @Test
    void testGetStateReturningDefault() {
        var board = mock(AbstractBoard.class);

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(Player.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        var state = game.getState();

        assertTrue(state instanceof DefaultGameState);
        assertEquals(GameState.Type.UNKNOWN, state.getType());
    }

    @Test
    void testGetStateReturningDrawn() {
        var board = mock(AbstractBoard.class);

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(Player.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        game.setFinishedAt(LocalDateTime.now());

        var state = game.getState();

        assertTrue(state instanceof DrawnGameState);
        assertEquals(GameState.Type.DRAWN_GAME, state.getType());
    }

    @Test
    void testGetStateReturningWhiteWin() {
        var board = mock(AbstractBoard.class);
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var whitePlayer = mock(Player.class);
        doNothing().when(whitePlayer).play();
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(Player.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var game = spy(new GameMock(whitePlayer, blackPlayer, board));
        when(game.hasNext())
            .thenReturn(false);

        game.setFinishedAt(LocalDateTime.now());
        game.run();

        var state = game.getState();

        assertTrue(state instanceof WhiteWinGameState);
        assertEquals(GameState.Type.WHITE_WIN, state.getType());
    }

    @Test
    void testGetStateReturningBlackWin() {
        var board = mock(AbstractBoard.class);
        when(board.getState())
            .thenReturn(agreedDefeatBoardState(board, Colors.BLACK));

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        doNothing()
            .when(whitePlayer).play();

        var blackPlayer = mock(Player.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        game.setFinishedAt(LocalDateTime.now());

        game.run();

        var state = game.getState();

        assertTrue(state instanceof BlackWinGameState);
        assertEquals(GameState.Type.BLACK_WIN, state.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPlayerAskedAction() {
        var board = mock(AbstractBoard.class);
        when(board.getState())
            .thenReturn(defaultBoardState(board, Colors.WHITE));

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(Player.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var journal = mock(Journal.class);

        var boardStateEvaluator = mock(BoardStateEvaluator.class);
        when(boardStateEvaluator.evaluate(any(Color.class)))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                if (Colors.WHITE.equals(color)) {
                    return defaultBoardState(board, color);
                }

                return checkMatedBoardState(board, color);
            });

        var game = new GameMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);
        game.run();

        verify(whitePlayer, times(1)).play();
        verify(blackPlayer, never()).play();
    }

    @Test
    void testGetWinnerByCheckMate() {
        var board = mock(AbstractBoard.class);
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(whitePlayer).getColor();
        doCallRealMethod().when(whitePlayer).getName();
        doNothing()
            .when(whitePlayer).play();

        var blackPlayer = new UserPlayer("test2", Colors.BLACK);

        var game = spy(new GameMock(whitePlayer, blackPlayer, board));
        when(game.hasNext())
            .thenReturn(false);

        game.run();

        var winner = game.getWinner();

        assertTrue(winner.isPresent());
        assertEquals(whitePlayer, winner.get());
    }

    @Test
    void testGetWinnerByAgreedDraw() {
        var board = mock(AbstractBoard.class);
        when(board.getState())
            .thenReturn(agreedDrawBoardState(board, Colors.WHITE));

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(whitePlayer).getColor();
        doNothing()
            .when(whitePlayer).play();

        var blackPlayer = new UserPlayer("test2", Colors.BLACK);

        var game = spy(new GameMock(whitePlayer, blackPlayer, board));
        when(game.hasNext())
            .thenReturn(false);

        game.run();

        var winner = game.getWinner();
        assertTrue(winner.isEmpty());
    }

    @Test
    void testGetWinnerByExitedDraw() {
        var board = mock(AbstractBoard.class);
        when(board.getState())
            .thenReturn(exitedBoardState(board, Colors.WHITE));

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(whitePlayer).getColor();
        doNothing()
            .when(whitePlayer).play();

        var blackPlayer = new UserPlayer("test2", Colors.BLACK);

        var game = spy(new GameMock(whitePlayer, blackPlayer, board));
        when(game.hasNext())
            .thenReturn(false);

        game.run();

        var winner = game.getWinner();
        assertTrue(winner.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPositiveGameFlow() {
        var board = spy(new StandardBoard());
        doCallRealMethod().when(board).notifyObservers(any());

        var journal = new JournalImpl();

        var boardStateEvaluator = mock(BoardStateEvaluator.class);
        when(boardStateEvaluator.evaluate(any()))
            .then(inv -> {
                var color = inv.getArgument(0, Color.class);
                if (journal.size() < 2) {
                    return defaultBoardState(board, color);
                }

                return staleMatedBoardState(board, color);
            });

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod().when(blackPlayer).play();

        var game = new GameMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

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
    @SuppressWarnings("unchecked")
    void testPositiveGameFlowWithWhiteUndo() {
        var board = spy(new StandardBoard());
        doCallRealMethod().when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod().when(blackPlayer).play();

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new UndoGameMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

        var whitePlayerInputObserver = new PlayerInputObserverInteratorMock(whitePlayer, game, "e2 e4", "undo", "d2 d4");
        var blackPlayerInputObserver = new PlayerInputObserverInteratorMock(blackPlayer, game, "e7 e5", "d7 d5");

        board.addObserver(whitePlayerInputObserver);
        board.addObserver(blackPlayerInputObserver);

        when(boardStateEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                if (Colors.WHITE.equals(color)) {
                    var iterator = whitePlayerInputObserver.getIterator();
                    if (!iterator.hasNext()) {
                        return staleMatedBoardState(board, color);
                    }
                }

                return defaultBoardState(board, color);
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

        verify(whitePlayer, times(2)).play();
        verify(blackPlayer, times(2)).play();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testNegativeGameFlowWithWhiteUndo() {
        var board = spy(new StandardBoard());
        doCallRealMethod().when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod().when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod().when(blackPlayer).play();

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new UndoGameMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

        var whitePlayerInputObserver = new PlayerInputObserverInteratorMock(
                whitePlayer, game, "undo", "e2 e4"
        );

        board.addObserver(whitePlayerInputObserver);
        board.addObserver(new PlayerInputObserverInteratorMock(blackPlayer, game, "e7 ", "e7 e5"));

        when(boardStateEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                if (Colors.WHITE.equals(color)) {
                    var iterator = whitePlayerInputObserver.getIterator();
                    if (!iterator.hasNext()) {
                        return staleMatedBoardState(board, color);
                    }
                }

                return defaultBoardState(board, color);
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

    @Test
    @SuppressWarnings("unchecked")
    void testPositiveGameDrawFlow() {
        var board = spy(new StandardBoard());
        doCallRealMethod()
            .when(board).getState();
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod()
            .when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).play();

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

        var whitePlayerInputObserver =
                new PlayerInputObserverInteratorMock(whitePlayer, game, "e2 e4");

        board.addObserver(whitePlayerInputObserver);
        board.addObserver(new PlayerInputObserverInteratorMock(blackPlayer, game, "draw"));

        when(boardStateEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                if (Colors.WHITE.equals(color)) {
                    var iterator = whitePlayerInputObserver.getIterator();
                    if (!iterator.hasNext()) {
                        return staleMatedBoardState(board, color);
                    }
                }

                return defaultBoardState(board, color);
            });

        Map<Class<? extends Event>, BiConsumer<Game,Event>> assertionMap = new HashMap<>();
        assertionMap.put(ActionTerminationEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof ActionTerminationEvent);
            assertEquals(blackPlayer, ((ActionTerminationEvent) evt).getPlayer());
            assertEquals(GameTerminationEvent.Type.DRAW, ((ActionTerminationEvent) evt).getType());
        });
        assertionMap.put(ActionTerminatedEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof ActionTerminatedEvent);
            assertEquals(blackPlayer, ((ActionTerminatedEvent) evt).getPlayer());
            assertEquals(GameTerminationEvent.Type.DRAW, ((ActionTerminatedEvent) evt).getType());
        });
        assertionMap.put(PlayerTerminateActionEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof PlayerTerminateActionEvent);
            assertEquals(blackPlayer, ((PlayerTerminateActionEvent) evt).getPlayer());
            assertEquals(GameTerminationEvent.Type.DRAW, ((PlayerTerminateActionEvent) evt).getType());
        });

        game.addObserver(new GameOutputObserverMock(game, assertionMap));
        game.run();

        assertEquals(1, game.getJournal().size());

        var winner = game.getWinner();
        assertTrue(winner.isEmpty());

        verify(whitePlayer, times(1)).play();
        verify(blackPlayer, times(1)).play();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testNegativeGameDrawFlow() {
        var board = spy(new StandardBoard());

        doCallRealMethod()
            .when(board).setState((BoardState) any(DefaultBoardState.class));
        doAnswer(new FirstExecutionExceptionAnswer<>(new RuntimeException("test")))
            .when(board).setState((BoardState) any(AgreedBoardState.class));

        doCallRealMethod()
            .when(board).getState();
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod()
            .when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).play();

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

        var whitePlayerInputObserver =
                new PlayerInputObserverInteratorMock(whitePlayer, game, "e2 e4");

        board.addObserver(whitePlayerInputObserver);
        board.addObserver(new PlayerInputObserverInteratorMock(blackPlayer, game, "draw"));

        when(boardStateEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                if (Colors.WHITE.equals(color)) {
                    var iterator = whitePlayerInputObserver.getIterator();
                    if (!iterator.hasNext()) {
                        return staleMatedBoardState(board, color);
                    }
                }

                return defaultBoardState(board, color);
            });

        Map<Class<? extends Event>, BiConsumer<Game,Event>> assertionMap = new HashMap<>();
        assertionMap.put(PlayerTerminateActionExceptionEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof PlayerTerminateActionExceptionEvent);
            assertEquals("test", ((PlayerTerminateActionExceptionEvent) evt).getMessage());
        });

        game.addObserver(new GameOutputObserverMock(game, assertionMap));
        game.run();

        assertEquals(1, journal.size());

        var winner = game.getWinner();
        assertTrue(winner.isEmpty());

        verify(whitePlayer, times(1)).play();
        verify(blackPlayer, times(1)).play();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPositiveGameExitFlow() {
        var board = spy(new StandardBoard());
        doCallRealMethod()
            .when(board).getState();
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod()
            .when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).play();

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

        var whitePlayerInputObserver =
                new PlayerInputObserverInteratorMock(whitePlayer, game, "e2 e4");

        board.addObserver(whitePlayerInputObserver);
        board.addObserver(new PlayerInputObserverInteratorMock(blackPlayer, game, "exit"));

        when(boardStateEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                if (Colors.WHITE.equals(color)) {
                    var iterator = whitePlayerInputObserver.getIterator();
                    if (!iterator.hasNext()) {
                        return staleMatedBoardState(board, color);
                    }
                }

                return defaultBoardState(board, color);
            });

        Map<Class<? extends Event>, BiConsumer<Game,Event>> assertionMap = new HashMap<>();
        assertionMap.put(ActionTerminationEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof ActionTerminationEvent);
            assertEquals(blackPlayer, ((ActionTerminationEvent) evt).getPlayer());
            assertEquals(GameTerminationEvent.Type.EXIT, ((ActionTerminationEvent) evt).getType());
        });
        assertionMap.put(ActionTerminatedEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof ActionTerminatedEvent);
            assertEquals(blackPlayer, ((ActionTerminatedEvent) evt).getPlayer());
            assertEquals(GameTerminationEvent.Type.EXIT, ((ActionTerminatedEvent) evt).getType());
        });
        assertionMap.put(PlayerTerminateActionEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof PlayerTerminateActionEvent);
            assertEquals(blackPlayer, ((PlayerTerminateActionEvent) evt).getPlayer());
            assertEquals(GameTerminationEvent.Type.EXIT, ((PlayerTerminateActionEvent) evt).getType());
        });

        game.addObserver(new GameOutputObserverMock(game, assertionMap));
        game.run();

        assertEquals(1, game.getJournal().size());

        var winner = game.getWinner();
        assertTrue(winner.isPresent());
        assertEquals(whitePlayer, winner.get());

        verify(whitePlayer, times(1)).play();
        verify(blackPlayer, times(1)).play();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testNegativeGameExitFlow() {
        var board = spy(new StandardBoard());

        doCallRealMethod()
            .when(board).setState((BoardState) any(DefaultBoardState.class));
        doAnswer(new FirstExecutionExceptionAnswer<>(new RuntimeException("test")))
            .when(board).setState((BoardState) any(ExitedBoardState.class));

        doCallRealMethod()
            .when(board).getState();
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod()
            .when(whitePlayer).play();

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).play();

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

        var whitePlayerInputObserver =
                new PlayerInputObserverInteratorMock(whitePlayer, game, "e2 e4");

        board.addObserver(whitePlayerInputObserver);
        board.addObserver(new PlayerInputObserverInteratorMock(blackPlayer, game, "exit"));

        when(boardStateEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                if (Colors.WHITE.equals(color)) {
                    var iterator = whitePlayerInputObserver.getIterator();
                    if (!iterator.hasNext()) {
                        return staleMatedBoardState(board, color);
                    }
                }

                return defaultBoardState(board, color);
            });

        Map<Class<? extends Event>, BiConsumer<Game,Event>> assertionMap = new HashMap<>();
        assertionMap.put(PlayerTerminateActionExceptionEvent.class, (gm, evt) -> {
            assertTrue(evt instanceof PlayerTerminateActionExceptionEvent);
            assertEquals("test", ((PlayerTerminateActionExceptionEvent) evt).getMessage());
        });

        game.addObserver(new GameOutputObserverMock(game, assertionMap));
        game.run();

        assertEquals(1, journal.size());

        var winner = game.getWinner();
        assertTrue(winner.isEmpty());

        verify(whitePlayer, times(1)).play();
        verify(blackPlayer, times(1)).play();
    }

    private static class PlayerInputObserverInteratorMock
            extends PlayerInputObserverMock {

        private final Iterator<String> actionInterator;

        PlayerInputObserverInteratorMock(Player player, Game game, String... actions) {
            super(player, game);
            this.actionInterator = List.of(actions).iterator();
        }

        public Iterator<String> getIterator() {
            return actionInterator;
        }

        @Override
        protected String getActionCommand() {
            return actionInterator.hasNext() ? actionInterator.next() : null;
        }
    }

    private static class FirstExecutionExceptionAnswer<T>
            implements Answer<T> {

        private final Exception exception;
        private boolean isExecuted;

        FirstExecutionExceptionAnswer(Exception exception) {
            this.exception = exception;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T answer(InvocationOnMock invocation) throws Throwable {
            if (!isExecuted) {
                isExecuted = true;
                throw exception;
            }

            return (T) invocation.callRealMethod();
        }
    }

    // keep using origin list of observers to be able to test player action re-ask properly
    private static class UndoGameMock extends AbstractPlayableGame {

        UndoGameMock(Player whitePlayer, Player blackPlayer,
                Board board, Journal<ActionMemento<?,?>> journal,
                BoardStateEvaluator<BoardState> boardStateEvaluator) {

            super(getLogger(GameMock.class), whitePlayer, blackPlayer,
                    board, journal, null, boardStateEvaluator, null
            );
        }
    }
}