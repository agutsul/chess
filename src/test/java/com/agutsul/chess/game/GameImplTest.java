package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDefeatBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedDrawBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.exitedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.event.GameTerminationEvent;
import com.agutsul.chess.game.observer.CloseableGameOverObserver;
import com.agutsul.chess.game.observer.GameExceptionObserver;
import com.agutsul.chess.game.observer.GameOverObserver;
import com.agutsul.chess.game.observer.GameStartedObserver;
import com.agutsul.chess.game.observer.SwitchPlayerObserver;
import com.agutsul.chess.game.state.BlackWinGameState;
import com.agutsul.chess.game.state.DefaultGameState;
import com.agutsul.chess.game.state.DrawnGameState;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.game.state.WhiteWinGameState;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.mock.PlayerActionObserverMock;
import com.agutsul.chess.mock.PlayerInputObserverMock;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;

@ExtendWith(MockitoExtension.class)
public class GameImplTest {

    @Test
    void testGetStateReturningDefault() {
        var board = mock(AbstractBoard.class);

        var whitePlayer = mock(UserPlayer.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(UserPlayer.class);
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

        var whitePlayer = mock(UserPlayer.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(UserPlayer.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        game.setFinishedAt(now());

        var state = game.getState();

        assertTrue(state instanceof DrawnGameState);
        assertEquals(GameState.Type.DRAWN_GAME, state.getType());
    }

    @Test
    void testGetStateReturningWhiteWin() {
        var board = spy(new StandardBoard());
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var whitePlayer = mock(UserPlayer.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(UserPlayer.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var journal = new JournalImpl();

        var game = new GameImpl(whitePlayer, blackPlayer, board, journal,
                new BoardStateEvaluatorImpl(board, journal),
                new GameContext()
        );

        game.setFinishedAt(now());
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

        var whitePlayer = mock(UserPlayer.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(UserPlayer.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var journal = new JournalImpl();

        var game = new GameImpl(whitePlayer, blackPlayer, board, journal,
                new BoardStateEvaluatorImpl(board, journal),
                new GameContext()
        );

        game.setFinishedAt(now());
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

        var whitePlayer = mock(UserPlayer.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(UserPlayer.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var journal = mock(Journal.class);

        var boardStateEvaluator = mock(BoardStateEvaluator.class);
        when(boardStateEvaluator.evaluate(any(Color.class)))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Colors.WHITE.equals(color)
                    ? defaultBoardState(board, color)
                    : checkMatedBoardState(board, color);
            });

        var game = new GameImpl(whitePlayer, blackPlayer,
                board, journal, boardStateEvaluator, new GameContext()
        );
        game.run();

        verify(whitePlayer, times(1)).notifyObservers(any());
        verify(blackPlayer, never()).notifyObservers(any());
    }

    @Test
    void testGetWinnerByCheckMate() {
        var board = mock(AbstractBoard.class);
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var whitePlayer = new UserPlayer("test1", Colors.WHITE);
        var blackPlayer = new UserPlayer("test2", Colors.BLACK);
        var journal = new JournalImpl();

        var game = new GameImpl(whitePlayer, blackPlayer, board, journal,
                new BoardStateEvaluatorImpl(board, journal),
                new GameContext()
        );

        game.run();

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isPresent());
        assertEquals(whitePlayer, winner.get());
    }

    @Test
    void testGetWinnerByAgreedDraw() {
        var board = mock(AbstractBoard.class);
        when(board.getState())
            .thenReturn(agreedDrawBoardState(board, Colors.WHITE));

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod()
            .when(whitePlayer).getColor();

        var blackPlayer = new UserPlayer("test2", Colors.BLACK);
        var journal = new JournalImpl();

        var game = spy(new GameImpl(whitePlayer, blackPlayer, board, journal,
                new BoardStateEvaluatorImpl(board, journal),
                new GameContext()
        ));

        when(game.hasNext())
            .thenReturn(false);

        game.run();

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isEmpty());
    }

    @Test
    void testGetWinnerByExit() {
        var board = spy(new StandardBoard());
        when(board.getState())
            .thenReturn(exitedBoardState(board, Colors.WHITE));

        var whitePlayer = new UserPlayer("test1", Colors.WHITE);
        var blackPlayer = new UserPlayer("test2", Colors.BLACK);
        var journal = new JournalImpl();

        var game = new GameImpl(whitePlayer, blackPlayer, board, journal,
                new BoardStateEvaluatorImpl(board, journal),
                new GameContext()
        );

        game.run();

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isPresent());
        assertEquals(blackPlayer, winner.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPositiveGameFlow() {
        var board = spy(new StandardBoard());
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var journal = new JournalImpl();

        var boardStateEvaluator = mock(BoardStateEvaluator.class);
        when(boardStateEvaluator.evaluate(any()))
            .then(inv -> {
                var color = inv.getArgument(0, Color.class);
                return journal.size() < 2
                    ? defaultBoardState(board, color)
                    : staleMatedBoardState(board, color);
            });

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod()
            .when(whitePlayer).notifyObservers(any());

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).notifyObservers(any());

        var game = new GameImpl(whitePlayer, blackPlayer,
                board, journal, boardStateEvaluator, new GameContext()
        );

        board.addObserver(new PlayerInputObserverInteratorMock(whitePlayer, game, "e2 e4"));
        board.addObserver(new PlayerInputObserverInteratorMock(blackPlayer, game, "e7 e5"));

        game.addObserver(new AbstractEventObserver<GameStartedEvent>() {

            @Override
            protected void process(GameStartedEvent event) {
                assertEquals(game, event.getGame());
            }
        });

        game.addObserver(new AbstractEventObserver<GameOverEvent>() {

            @Override
            protected void process(GameOverEvent event) {
                assertEquals(game, event.getGame());
            }
        });

        game.addObserver(new AbstractEventObserver<ActionPerformedEvent>() {

            @Override
            protected void process(ActionPerformedEvent event) {
                assertNotNull(event.getActionMemento());
            }
        });

        game.addObserver(new AbstractEventObserver<ActionExecutionEvent>() {

            @Override
            protected void process(ActionExecutionEvent event) {
                var action = event.getAction();
                assertTrue(action instanceof PieceMoveAction);
            }
        });

        game.run();

        assertEquals(2, game.getJournal().size());
        assertTrue(game.getWinnerPlayer().isEmpty());

        verify(whitePlayer, times(1)).notifyObservers(any());
        verify(blackPlayer, times(1)).notifyObservers(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPositiveGameFlowWithWhiteUndo() {
        var board = spy(new StandardBoard());
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod()
            .when(whitePlayer).notifyObservers(any());

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).notifyObservers(any());

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameImpl(whitePlayer, blackPlayer,
                board, journal, boardStateEvaluator, new GameContext()
        );

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

        game.addObserver(new AbstractEventObserver<ActionCancellingEvent>() {

            @Override
            protected void process(ActionCancellingEvent event) {
                var action = event.getAction();
                assertTrue(action instanceof CancelMoveAction);
            }
        });

        var cancelledColors = new HashSet<Color>();
        game.addObserver(new AbstractEventObserver<ActionCancelledEvent>() {

            @Override
            protected void process(ActionCancelledEvent event) {
                cancelledColors.add(event.getColor());
            }
        });

        game.run();

        assertEquals(2, cancelledColors.size());
        assertEquals(2, game.getJournal().size());
        assertTrue(game.getWinnerPlayer().isEmpty());

        verify(whitePlayer, times(2)).notifyObservers(any());
        verify(blackPlayer, times(2)).notifyObservers(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testNegativeGameFlowWithWhiteUndo() {
        var board = spy(new StandardBoard());
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var whitePlayer = spy(new UserPlayer("test1", Colors.WHITE));
        doCallRealMethod()
            .when(whitePlayer).notifyObservers(any());

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).notifyObservers(any());

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameImpl(whitePlayer, blackPlayer,
                board, journal, boardStateEvaluator, new GameContext()
        );

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

        game.addObserver(new AbstractEventObserver<PlayerCancelActionExceptionEvent>() {

            @Override
            protected void process(PlayerCancelActionExceptionEvent event) {
                assertEquals("No action to cancel", event.getMessage());
            }
        });

        game.addObserver(new AbstractEventObserver<PlayerActionExceptionEvent>() {

            @Override
            protected void process(PlayerActionExceptionEvent event) {
                assertEquals("Invalid action format: 'e7 '", event.getMessage());
            }
        });

        game.run();

        assertEquals(2, game.getJournal().size());
        assertTrue(game.getWinnerPlayer().isEmpty());

        verify(whitePlayer, times(1)).notifyObservers(any());
        verify(blackPlayer, times(1)).notifyObservers(any());
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
            .when(whitePlayer).notifyObservers(any());

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).notifyObservers(any());

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameImpl(whitePlayer, blackPlayer,
                board, journal, boardStateEvaluator, new GameContext()
        );

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

        game.addObserver(new AbstractEventObserver<ActionTerminationEvent>() {

            @Override
            protected void process(ActionTerminationEvent event) {
                assertEquals(blackPlayer, event.getPlayer());
                assertEquals(GameTerminationEvent.Type.DRAW, event.getType());
            }
        });

        game.addObserver(new AbstractEventObserver<ActionTerminatedEvent>() {

            @Override
            protected void process(ActionTerminatedEvent event) {
                assertEquals(blackPlayer, event.getPlayer());
                assertEquals(GameTerminationEvent.Type.DRAW, event.getType());
            }
        });

        game.addObserver(new AbstractEventObserver<PlayerTerminateActionEvent>() {

            @Override
            protected void process(PlayerTerminateActionEvent event) {
                assertEquals(blackPlayer, event.getPlayer());
                assertEquals(GameTerminationEvent.Type.DRAW, event.getType());
            }
        });

        game.run();

        assertEquals(1, game.getJournal().size());

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isEmpty());

        verify(whitePlayer, times(1)).notifyObservers(any());
        verify(blackPlayer, times(1)).notifyObservers(any());
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
            .when(whitePlayer).notifyObservers(any());

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).notifyObservers(any());

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameExceptionMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

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

        game.addObserver(new AbstractEventObserver<PlayerTerminateActionExceptionEvent>() {

            @Override
            protected void process(PlayerTerminateActionExceptionEvent event) {
                assertEquals("test", event.getMessage());
            }
        });

        game.run();

        assertEquals(1, journal.size());

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isEmpty());

        verify(whitePlayer, times(1)).notifyObservers(any());
        verify(blackPlayer, times(1)).notifyObservers(any());
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
            .when(whitePlayer).notifyObservers(any());

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).notifyObservers(any());

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameImpl(whitePlayer, blackPlayer, board, journal,
                boardStateEvaluator, new GameContext()
        );

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

        game.addObserver(new AbstractEventObserver<ActionTerminationEvent>() {

            @Override
            protected void process(ActionTerminationEvent event) {
                assertEquals(blackPlayer, event.getPlayer());
                assertEquals(GameTerminationEvent.Type.EXIT, event.getType());
            }
        });

        game.addObserver(new AbstractEventObserver<ActionTerminatedEvent>() {

            @Override
            protected void process(ActionTerminatedEvent event) {
                assertEquals(blackPlayer, event.getPlayer());
                assertEquals(GameTerminationEvent.Type.EXIT, event.getType());
            }
        });

        game.addObserver(new AbstractEventObserver<PlayerTerminateActionEvent>() {

            @Override
            protected void process(PlayerTerminateActionEvent event) {
                assertEquals(blackPlayer, event.getPlayer());
                assertEquals(GameTerminationEvent.Type.EXIT, event.getType());
            }
        });

        game.run();

        assertEquals(1, game.getJournal().size());

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isPresent());
        assertEquals(whitePlayer, winner.get());

        verify(whitePlayer, times(1)).notifyObservers(any());
        verify(blackPlayer, times(1)).notifyObservers(any());
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
            .when(whitePlayer).notifyObservers(any());

        var blackPlayer = spy(new UserPlayer("test2", Colors.BLACK));
        doCallRealMethod()
            .when(blackPlayer).notifyObservers(any());

        var journal = new JournalImpl();
        var boardStateEvaluator = mock(BoardStateEvaluator.class);

        var game = new GameExceptionMock(whitePlayer, blackPlayer, board, journal, boardStateEvaluator);

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

        game.addObserver(new AbstractEventObserver<PlayerTerminateActionExceptionEvent>() {

            @Override
            protected void process(PlayerTerminateActionExceptionEvent event) {
                assertEquals("test", event.getMessage());
            }
        });

        game.run();

        assertEquals(1, journal.size());

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isEmpty());

        verify(whitePlayer, times(1)).notifyObservers(any());
        verify(blackPlayer, times(1)).notifyObservers(any());
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
        protected String getActionCommand(Optional<Long> timeout) {
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

    private static class GameExceptionMock
            extends GameImpl {

        GameExceptionMock(Player whitePlayer, Player blackPlayer, Board board, Journal<ActionMemento<?, ?>> journal,
                BoardStateEvaluator<BoardState> boardStateEvaluator) {

            super(whitePlayer, blackPlayer, board, journal, boardStateEvaluator, new GameContext());
        }

        @Override
        protected void initObservers() {
            Stream.of(
                    new CloseableGameOverObserver(getContext()),
                    new GameStartedObserver(),
                    new GameOverObserver(),
                    new PlayerActionObserverMock(this),
                    new SwitchPlayerObserver(this),
                    new PostActionEventObserver(),
                    new GameWinnerObserver(this),
                    new GameExceptionObserver()
            ).forEach(this::addObserver);
        }
    }
}