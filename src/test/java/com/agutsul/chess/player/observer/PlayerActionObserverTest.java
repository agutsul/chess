package com.agutsul.chess.player.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.event.ActionCancelledEvent;
import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.activity.action.memento.ActionMementoMock;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PlayerActionObserverTest {

    @Mock
    AbstractPlayableGame game;
    @Mock
    AbstractBoard board;

    @InjectMocks
    PlayerActionObserver observer;

    @BeforeEach
    void setUp() {
        when(game.getBoard())
            .thenReturn(board);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPlayerActionEvent() {
        var piece = mock(PawnPiece.class);
        when(piece.getType())
            .thenReturn(Piece.Type.PAWN);

        var position = mock(Position.class);

        var action = spy(new PieceMoveAction<>(piece, position));
        doCallRealMethod()
            .when(action).getType();
        doCallRealMethod()
            .when(action).getPosition();
        doCallRealMethod()
            .when(action).getPiece();
        doCallRealMethod()
            .when(action).getSource();

        when(board.getPiece(anyString()))
            .thenReturn(Optional.of(piece));
        when(board.getPosition(anyString()))
            .thenReturn(Optional.of(position));
        when(board.getActions(any()))
            .thenReturn(List.of(action));

        observer.observe(new PlayerActionEvent(mock(Player.class), "a2", "a3"));

        verify(game, times(2)).notifyObservers(any());
    }

    @Test
    void testPlayerActionEventException() {
        when(board.getPiece(anyString()))
            .thenReturn(Optional.empty());

        observer.observe(new PlayerActionEvent(mock(Player.class), "a2", "a3"));

        verify(game, times(1)).notifyObservers(any(PlayerActionExceptionEvent.class));
        verify(board, times(1)).notifyObservers(any(RequestPlayerActionEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPlayerCancelActionEventWithEmptyJournal() {
        var journal = mock(Journal.class);
        when(journal.isEmpty())
            .thenReturn(true);

        when(game.getJournal())
            .thenReturn(journal);

        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        observer.observe(new PlayerCancelActionEvent(player));

        verify(game, times(1)).notifyObservers(any(PlayerCancelActionExceptionEvent.class));
        verify(board, times(1)).notifyObservers(any(RequestPlayerActionEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPlayerCancelActionEvent() {
        when(board.getPiece(anyString()))
            .thenReturn(Optional.of(mock(PawnPiece.class)));
        when(board.getPosition(anyString()))
            .thenReturn(Optional.of(mock(Position.class)));

        var journal = new JournalImpl();
        journal.add(mockActionMemento(Colors.WHITE));
        journal.add(mockActionMemento(Colors.BLACK));

        when(game.getJournal())
            .thenReturn(journal);

        // emulate removing record from journal after successful execution of cancel action
        doAnswer(inv -> {
            var arg = inv.getArgument(0);
            if (arg instanceof ActionCancelledEvent) {
                journal.removeLast();
            }
            return null;
        }).when(game).notifyObservers(any());

        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        observer.observe(new PlayerCancelActionEvent(player));

        verify(game, atLeast(2)).notifyObservers(any());
    }

    @Test
    void testPlayerDrawActionEvent() {
        doAnswer(inv -> {
            var state = inv.getArgument(0, BoardState.class);
            assertEquals(BoardState.Type.AGREED_DRAW, state.getType());

            return null;
        }).when(board).setState(any());

        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        observer.observe(new PlayerTerminateActionEvent(player, Type.DRAW));

        verify(game, times(1)).notifyObservers(any(ActionTerminationEvent.class));
        verify(game, times(1)).notifyObservers(any(ActionTerminatedEvent.class));
    }

    @Test
    void testPlayerDrawActionExceptionEvent() {
        doThrow(RuntimeException.class)
            .when(board).setState(any());

        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        observer.observe(new PlayerTerminateActionEvent(player, Type.DRAW));

        verify(game, times(1)).notifyObservers(any(PlayerTerminateActionExceptionEvent.class));
        verify(board, times(1)).notifyObservers(any(RequestPlayerActionEvent.class));
    }

    private static ActionMemento<String,String> mockActionMemento(Color color) {
        return new ActionMementoMock<>(color, Action.Type.MOVE, Piece.Type.PAWN, "src", "trg");
    }
}