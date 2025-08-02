package com.agutsul.chess.board.state;

import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.DEFAULT;
import static com.agutsul.chess.board.state.BoardState.Type.FIFTY_MOVES;
import static com.agutsul.chess.board.state.BoardState.Type.INSUFFICIENT_MATERIAL;
import static com.agutsul.chess.board.state.BoardState.Type.SEVENTY_FIVE_MOVES;
import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.fiftyMovesBoardState;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class CompositeBoardStateTest {

    @Mock
    Board board;

    @Mock
    Color color;

    @Test
    void testCompositeBoardStateCreation() {
        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new CompositeBoardState(emptyList())
        );

        assertEquals(
                "Unable to set empty board states",
                thrown.getMessage()
        );
    }

    @Test
    void testGetBoardStates() {
        var checkedBoardState = checkedBoardState(board, color);
        var fiftyMovesBoardState = fiftyMovesBoardState(board, color);

        var compositeBoardState = new CompositeBoardState(List.of(
                checkedBoardState,
                fiftyMovesBoardState
        ));

        var boardStates = new ArrayList<>(compositeBoardState.getBoardStates());

        assertEquals(2, boardStates.size());
        assertEquals(checkedBoardState, boardStates.getFirst());
        assertEquals(fiftyMovesBoardState, boardStates.getLast());
    }

    @Test
    void testGetColor() {
        var checkedBoardState = checkedBoardState(board, color);
        var checkedBoardStateMock = spy(checkedBoardState);
        doCallRealMethod().when(checkedBoardStateMock).getColor();

        var fiftyMovesBoardState = fiftyMovesBoardState(board, color);
        var fiftyMovesBoardStateMock = spy(fiftyMovesBoardState);

        var compositeBoardState = new CompositeBoardState(List.of(
                checkedBoardStateMock,
                fiftyMovesBoardStateMock
        ));

        assertEquals(color, compositeBoardState.getColor());

        verify(checkedBoardStateMock, times(1)).getColor();
        verify(fiftyMovesBoardStateMock, never()).getColor();
    }

    @Test
    void testGetType() {
        var checkedBoardState = checkedBoardState(board, color);
        var checkedBoardStateMock = spy(checkedBoardState);
        doCallRealMethod().when(checkedBoardStateMock).getType();

        var fiftyMovesBoardState = fiftyMovesBoardState(board, color);
        var fiftyMovesBoardStateMock = spy(fiftyMovesBoardState);

        var compositeBoardState = new CompositeBoardState(List.of(
                checkedBoardStateMock,
                fiftyMovesBoardStateMock
        ));

        assertEquals(CHECKED, compositeBoardState.getType());

        verify(checkedBoardStateMock, times(1)).getType();
        verify(fiftyMovesBoardStateMock, never()).getType();
    }

    @Test
    void testIsType() {
        var checkedBoardState = checkedBoardState(board, color);
        var checkedBoardStateMock = spy(checkedBoardState);
        doCallRealMethod().when(checkedBoardStateMock).isType(any());

        var fiftyMovesBoardState = fiftyMovesBoardState(board, color);
        var fiftyMovesBoardStateMock = spy(fiftyMovesBoardState);
        doCallRealMethod().when(fiftyMovesBoardStateMock).isType(any());

        var compositeBoardState = new CompositeBoardState(List.of(
                checkedBoardStateMock,
                fiftyMovesBoardStateMock
        ));

        assertTrue(compositeBoardState.isType(CHECKED));
        assertTrue(compositeBoardState.isType(FIFTY_MOVES));

        assertFalse(compositeBoardState.isType(CHECK_MATED));
        assertFalse(compositeBoardState.isType(SEVENTY_FIVE_MOVES));

        verify(checkedBoardStateMock, times(4)).isType(any());
        verify(fiftyMovesBoardStateMock, times(3)).isType(any());
    }

    @Test
    void testIsAnyType() {
        var checkedBoardState = checkedBoardState(board, color);
        var checkedBoardStateMock = spy(checkedBoardState);
        doCallRealMethod().when(checkedBoardStateMock).isAnyType(any(),any());

        var fiftyMovesBoardState = fiftyMovesBoardState(board, color);
        var fiftyMovesBoardStateMock = spy(fiftyMovesBoardState);
        doCallRealMethod().when(fiftyMovesBoardStateMock).isAnyType(any(),any());

        var compositeBoardState = new CompositeBoardState(List.of(
                checkedBoardStateMock,
                fiftyMovesBoardStateMock
        ));

        assertTrue(compositeBoardState.isAnyType(CHECKED, SEVENTY_FIVE_MOVES));
        assertTrue(compositeBoardState.isAnyType(CHECK_MATED, FIFTY_MOVES));

        assertFalse(compositeBoardState.isAnyType(DEFAULT, INSUFFICIENT_MATERIAL));

        verify(checkedBoardStateMock, times(3)).isAnyType(any(),any());
        verify(fiftyMovesBoardStateMock, times(2)).isAnyType(any(),any());
    }

    @Test
    void testGetActions() {
        var checkedBoardState = checkedBoardState(board, color);
        var checkedBoardStateMock = spy(checkedBoardState);
        doCallRealMethod().when(checkedBoardStateMock).getActions(any());

        var fiftyMovesBoardState = fiftyMovesBoardState(board, color);
        var fiftyMovesBoardStateMock = spy(fiftyMovesBoardState);

        var compositeBoardState = new CompositeBoardState(List.of(
                checkedBoardStateMock,
                fiftyMovesBoardStateMock
        ));

        assertTrue(compositeBoardState.getActions(mock(Piece.class)).isEmpty());

        verify(checkedBoardStateMock, times(1)).getActions(any());
        verify(fiftyMovesBoardStateMock, never()).getActions(any());
    }

    @Test
    void testGetImpacts() {
        var checkedBoardState = checkedBoardState(board, color);
        var checkedBoardStateMock = spy(checkedBoardState);
        doCallRealMethod().when(checkedBoardStateMock).getImpacts(any());

        var fiftyMovesBoardState = fiftyMovesBoardState(board, color);
        var fiftyMovesBoardStateMock = spy(fiftyMovesBoardState);

        var compositeBoardState = new CompositeBoardState(List.of(
                checkedBoardStateMock,
                fiftyMovesBoardStateMock
        ));

        assertTrue(compositeBoardState.getImpacts(mock(Piece.class)).isEmpty());

        verify(checkedBoardStateMock, times(1)).getImpacts(any());
        verify(fiftyMovesBoardStateMock, never()).getImpacts(any());
    }

    @Test
    void testToString() {
        when(color.toString())
            .thenReturn(Colors.WHITE.toString());

        var checkedBoardState = checkedBoardState(board, color);
        var fiftyMovesBoardState = fiftyMovesBoardState(board, color);

        var compositeBoardState = new CompositeBoardState(List.of(
                checkedBoardState,
                fiftyMovesBoardState
        ));

        assertEquals("(CHECKED:WHITE,FIFTY_MOVES:WHITE):WHITE", compositeBoardState.toString());
    }
}