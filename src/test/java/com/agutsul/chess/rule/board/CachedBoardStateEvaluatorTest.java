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
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CachedBoardStateEvaluatorTest {

    @Test
    @SuppressWarnings("unchecked")
    void testGetCachedBoardState() {
        var board = mock(AbstractBoard.class);

        var boardStateEvaluator = mock(BoardStateEvaluator.class);
        when(boardStateEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return new CheckedBoardState(board, color);
            });

        var evaluator = new CachedBoardStateEvaluator(board, boardStateEvaluator);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertEquals(BoardState.Type.CHECKED, boardState.getType());

        var boardState2 = evaluator.evaluate(Colors.WHITE);

        assertEquals(BoardState.Type.CHECKED, boardState2.getType());
        assertEquals(boardState, boardState2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCachedBoardStateCleared() {
        var board = spy(new StandardBoard());
        doCallRealMethod().when(board).notifyObservers(any());

        var boardStateEvaluator = mock(BoardStateEvaluator.class);
        when(boardStateEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return new CheckMatedBoardState(board, color);
            });

        var evaluator = new CachedBoardStateEvaluator(board, boardStateEvaluator);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertEquals(BoardState.Type.CHECK_MATED, boardState.getType());

        board.notifyObservers(new ActionCancelledEvent());

        var boardState2 = evaluator.evaluate(Colors.WHITE);
        assertEquals(BoardState.Type.CHECK_MATED, boardState2.getType());
        assertNotEquals(boardState, boardState2);
    }
}