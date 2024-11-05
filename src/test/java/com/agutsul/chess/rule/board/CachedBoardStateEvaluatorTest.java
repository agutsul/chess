package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CachedBoardStateEvaluatorTest {

    @Test
    void testGetCachedBoardState() {
        var board = mock(AbstractBoard.class);
        when(board.isChecked(any()))
            .thenReturn(true);
        when(board.isCheckMated(any()))
            .thenReturn(false);

        var evaluator = new CachedBoardStateEvaluator(board);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertEquals(BoardState.Type.CHECKED, boardState.getType());

        var boardState2 = evaluator.evaluate(Colors.WHITE);

        assertEquals(BoardState.Type.CHECKED, boardState2.getType());
        assertEquals(boardState, boardState2);
    }

    @Test
    void testCachedBoardStateCleared() {
        var board = spy(new StandardBoard());
        doCallRealMethod().when(board).notifyObservers(any());

        when(board.isChecked(any()))
            .thenReturn(true);
        when(board.isCheckMated(any()))
            .thenReturn(false);

        var evaluator = new CachedBoardStateEvaluator(board);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertEquals(BoardState.Type.CHECKED, boardState.getType());

        board.notifyObservers(new ActionCancelledEvent());

        var boardState2 = evaluator.evaluate(Colors.WHITE);
        assertEquals(BoardState.Type.CHECKED, boardState2.getType());
        assertNotEquals(boardState, boardState2);
    }
}