package com.agutsul.chess.ai;

import static com.agutsul.chess.ai.SelectionStrategy.Type.ALPHA_BETA;
import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class ActionSelectionStrategyTest {

    @Test
    @SuppressWarnings("unchecked")
    void testSelectActionWithNullExecutor() {
        var selectionStrategy = new ActionSelectionStrategy(
                mock(Board.class), mock(Journal.class), null, ALPHA_BETA
        );

        var thrown = assertThrows(
                IllegalStateException.class,
                () -> selectionStrategy.select(Colors.WHITE)
        );

        assertEquals(
                "Unable to select action for 'WHITE': fork-join pool not set",
                thrown.getMessage()
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSelectionWithoutAnyActionFound() {
        var piece = mock(Piece.class);

        var board = mock(Board.class);
        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(piece));
        when(board.getActions(any(Piece.class)))
            .thenReturn(emptyList());

        var selectionStrategy = new ActionSelectionStrategy(
                board, mock(Journal.class), mock(ForkJoinPool.class), ALPHA_BETA
        );

        var result = selectionStrategy.select(Colors.WHITE);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @EnumSource(SelectionStrategy.Type.class)
    void testSelectionActionFound(SelectionStrategy.Type type) {
        var action = mock(Action.class);
        var piece = mock(Piece.class);

        var board = mock(Board.class);
        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(piece));
        when(board.getActions(any(Piece.class)))
            .thenReturn(List.of(action));

        var journal = mock(Journal.class);

        var forkJoinPool = mock(ForkJoinPool.class);
        when(forkJoinPool.invoke(any()))
            .thenReturn(new ActionSimulationResult<>(board, journal, action, null, 0));

        var selectionStrategy = new ActionSelectionStrategy(board, journal, forkJoinPool, type);
        var result = selectionStrategy.select(Colors.WHITE);

        assertTrue(result.isPresent());
        assertEquals(action, result.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSelectionActionException() {
        var action = mock(Action.class);
        var piece = mock(Piece.class);

        var board = mock(Board.class);
        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(piece));
        when(board.getActions(any(Piece.class)))
            .thenReturn(List.of(action));

        var journal = mock(Journal.class);

        var forkJoinPool = mock(ForkJoinPool.class);
        when(forkJoinPool.invoke(any()))
            .thenThrow(new RuntimeException("test"));

        var selectionStrategy = new ActionSelectionStrategy(
                board, journal, forkJoinPool, ALPHA_BETA
        );

        var thrown = assertThrows(
                RuntimeException.class,
                () -> selectionStrategy.select(Colors.WHITE)
        );

        assertEquals("test", thrown.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSearchActionWithNullExecutor() {
        var selectionStrategy = new ActionSelectionStrategy(
                mock(Board.class), mock(Journal.class), null, ALPHA_BETA
        );

        var thrown = assertThrows(
                IllegalStateException.class,
                () -> selectionStrategy.select(Colors.WHITE, BoardState.Type.CHECK_MATED)
        );

        assertEquals(
                "Unable to select action for 'WHITE' and board state 'CHECK_MATED': fork-join pool not set",
                thrown.getMessage()
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSearchWithoutAnyActionFound() {
        var piece = mock(Piece.class);

        var board = mock(Board.class);
        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(piece));
        when(board.getActions(any(Piece.class)))
            .thenReturn(emptyList());

        var selectionStrategy = new ActionSelectionStrategy(
                board, mock(Journal.class), mock(ForkJoinPool.class), ALPHA_BETA
        );

        var result = selectionStrategy.select(Colors.WHITE, BoardState.Type.CHECK_MATED);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @EnumSource(SelectionStrategy.Type.class)
    void testSearchActionFound(SelectionStrategy.Type type) {
        var action = mock(Action.class);
        var piece = mock(Piece.class);

        var board = mock(Board.class);
        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(piece));
        when(board.getActions(any(Piece.class)))
            .thenReturn(List.of(action));
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var memento = mock(ActionMemento.class);
        when(memento.getColor())
            .thenReturn(Colors.WHITE);

        var journal = mock(Journal.class);
        when(journal.getLast())
            .thenReturn(memento);

        var forkJoinPool = mock(ForkJoinPool.class);
        when(forkJoinPool.invoke(any()))
            .thenReturn(new ActionSimulationResult<>(board, journal, action, Colors.WHITE, 0));

        var selectionStrategy = new ActionSelectionStrategy(board, journal, forkJoinPool, type);
        var result = selectionStrategy.select(Colors.WHITE, BoardState.Type.CHECK_MATED);

        assertTrue(result.isPresent());
        assertEquals(action, result.get());
    }

    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @EnumSource(SelectionStrategy.Type.class)
    void testSearchActionNotFound(SelectionStrategy.Type type) {
        var action = mock(Action.class);
        var piece = mock(Piece.class);

        var board = mock(Board.class);
        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(piece));
        when(board.getActions(any(Piece.class)))
            .thenReturn(List.of(action));
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var memento = mock(ActionMemento.class);
        when(memento.getColor())
            .thenReturn(Colors.BLACK);

        var journal = mock(Journal.class);
        when(journal.getLast())
            .thenReturn(memento);

        var forkJoinPool = mock(ForkJoinPool.class);
        when(forkJoinPool.invoke(any()))
            .thenReturn(new ActionSimulationResult<>(board, journal, action, Colors.WHITE, 0));

        var selectionStrategy = new ActionSelectionStrategy(board, journal, forkJoinPool, type);
        var result = selectionStrategy.select(Colors.WHITE, BoardState.Type.CHECK_MATED);

        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSearchActionException() {
        var action = mock(Action.class);
        var piece = mock(Piece.class);

        var board = mock(Board.class);
        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(piece));
        when(board.getActions(any(Piece.class)))
            .thenReturn(List.of(action));

        var journal = mock(Journal.class);

        var forkJoinPool = mock(ForkJoinPool.class);
        when(forkJoinPool.invoke(any()))
            .thenThrow(new RuntimeException("test"));

        var selectionStrategy = new ActionSelectionStrategy(
                board, journal, forkJoinPool, ALPHA_BETA
        );

        var thrown = assertThrows(
                RuntimeException.class,
                () -> selectionStrategy.select(Colors.WHITE, BoardState.Type.CHECK_MATED)
        );

        assertEquals("test", thrown.getMessage());
    }
}