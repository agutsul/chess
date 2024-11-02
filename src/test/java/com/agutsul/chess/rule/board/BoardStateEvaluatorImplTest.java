package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.board.state.StaleMatedBoardState;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class BoardStateEvaluatorImplTest {

    @Test
    void evaluateCheckedBoardState() {
        var board = mock(Board.class);

        when(board.isChecked(any()))
            .thenReturn(true);
        when(board.isCheckMated(any()))
            .thenReturn(false);

        var evaluator = new BoardStateEvaluatorImpl(board);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState instanceof CheckedBoardState);
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateCheckMatedBoardState() {
        var board = mock(Board.class);

        when(board.isChecked(any()))
            .thenReturn(true);
        when(board.isCheckMated(any()))
            .thenReturn(true);

        var evaluator = new BoardStateEvaluatorImpl(board);
        var boardState = evaluator.evaluate(Colors.BLACK);

        assertTrue(boardState instanceof CheckMatedBoardState);
        assertEquals(Colors.BLACK, boardState.getColor());
    }

    @Test
    void evaluateStaleMatedBoardState() {
        var board = mock(Board.class);

        when(board.isChecked(any()))
            .thenReturn(false);
        when(board.isStaleMated(any()))
            .thenReturn(true);

        var evaluator = new BoardStateEvaluatorImpl(board);
        var boardState = evaluator.evaluate(Colors.BLACK);

        assertTrue(boardState instanceof StaleMatedBoardState);
        assertEquals(Colors.BLACK, boardState.getColor());
    }

    @Test
    void evaluateDefaultBoardState() {
        var board = mock(Board.class);

        when(board.isChecked(any()))
            .thenReturn(false);
        when(board.isStaleMated(any()))
            .thenReturn(false);

        var evaluator = new BoardStateEvaluatorImpl(board);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState instanceof DefaultBoardState);
        assertEquals(Colors.WHITE, boardState.getColor());
    }
}