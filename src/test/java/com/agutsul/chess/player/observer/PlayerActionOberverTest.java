package com.agutsul.chess.player.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.action.event.DrawExecutionEvent;
import com.agutsul.chess.action.event.DrawPerformedEvent;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.action.memento.ActionMementoMock;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PlayerActionOberverTest {

    @Test
    void testNoProcessingEvent() {
        var game = mock(AbstractGame.class);
        var observer = new PlayerActionOberver(game);
        observer.observe(mock(Event.class));

        verify(game, never()).notifyObservers(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPlayerActionEvent() {
        var piece = mock(PawnPiece.class);
        var position = mock(Position.class);

        var action = mock(PieceMoveAction.class);
        when(action.getType())
            .thenReturn(Action.Type.MOVE);
        when(action.getSource())
            .thenReturn(piece);
        when(action.getPosition())
            .thenReturn(position);

        var board = mock(Board.class);
        when(board.getPiece(anyString()))
            .thenReturn(Optional.of(piece));
        when(board.getPosition(anyString()))
            .thenReturn(Optional.of(position));
        when(board.getActions(any()))
            .thenReturn(List.of(action));

        var game = mock(AbstractGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var observer = new PlayerActionOberver(game);
        observer.observe(new PlayerActionEvent(mock(Player.class), "a2", "a3"));

        verify(game, times(2)).notifyObservers(any());
    }

    @Test
    void testPlayerActionEventException() {
        var board = mock(AbstractBoard.class);
        when(board.getPiece(anyString()))
            .thenReturn(Optional.empty());

        var game = mock(AbstractGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var observer = new PlayerActionOberver(game);
        observer.observe(new PlayerActionEvent(mock(Player.class), "a2", "a3"));

        verify(game, times(1)).notifyObservers(any(PlayerActionExceptionEvent.class));
        verify(board, times(1)).notifyObservers(any(RequestPlayerActionEvent.class));
    }

    @Test
    void testPlayerCancelActionEventWithEmptyJournal() {
        var board = mock(AbstractBoard.class);

        var game = mock(AbstractGame.class);
        when(game.hasPrevious())
            .thenReturn(Boolean.FALSE);
        when(game.getBoard())
            .thenReturn(board);

        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerActionOberver(game);
        observer.observe(new PlayerCancelActionEvent(player));

        verify(game, times(1)).notifyObservers(any(PlayerCancelActionExceptionEvent.class));
        verify(board, times(1)).notifyObservers(any(RequestPlayerActionEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPlayerCancelActionEvent() {
        var board = mock(AbstractBoard.class);
        when(board.getPiece(anyString()))
            .thenReturn(Optional.of(mock(PawnPiece.class)));
        when(board.getPosition(anyString()))
            .thenReturn(Optional.of(mock(Position.class)));

        var journal = new JournalImpl<Memento>();
        journal.add(mockActionMemento(Colors.WHITE));
        journal.add(mockActionMemento(Colors.BLACK));

        var game = mock(AbstractGame.class);
        when(game.hasPrevious())
            .thenReturn(Boolean.TRUE);
        when(game.getBoard())
            .thenReturn(board);
        when(game.getJournal())
            .thenReturn(journal);

        // emulate removing record from journal after successful execution of cancel action
        doAnswer(inv -> {
            var arg = inv.getArgument(0);
            if (arg instanceof ActionCancelledEvent) {
                journal.remove(journal.size() - 1);
            }
            return null;
        }).when(game).notifyObservers(any());

        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerActionOberver(game);
        observer.observe(new PlayerCancelActionEvent(player));

        verify(game, atLeast(2)).notifyObservers(any());
    }

    @Test
    void testPlayerDrawActionEvent() {
        var board = mock(AbstractBoard.class);
        doAnswer(inv -> {
            var state = inv.getArgument(0, BoardState.class);
            assertEquals(BoardState.Type.AGREED_DRAW, state.getType());

            return null;
        }).when(board).setState(any());

        var game = mock(AbstractGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerActionOberver(game);
        observer.observe(new PlayerDrawActionEvent(player));

        verify(game, times(1)).notifyObservers(any(DrawExecutionEvent.class));
        verify(game, times(1)).notifyObservers(any(DrawPerformedEvent.class));
    }

    @Test
    void testPlayerDrawActionExceptionEvent() {
        var board = mock(AbstractBoard.class);
        doThrow(RuntimeException.class)
            .when(board).setState(any());

        var game = mock(AbstractGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerActionOberver(game);
        observer.observe(new PlayerDrawActionEvent(player));

        verify(game, times(1)).notifyObservers(any(PlayerDrawActionExceptionEvent.class));
        verify(board, times(1)).notifyObservers(any(RequestPlayerActionEvent.class));
    }

    private static ActionMemento<String,String> mockActionMemento(Color color) {
        return new ActionMementoMock<>(color, Action.Type.MOVE, Piece.Type.PAWN, "src", "trg");
    }
}