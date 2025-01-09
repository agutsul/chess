package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CheckableBoardStateEvaluatorTest {

    @Test
    void testNonCheckedBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);

        var evaluator = new CheckableBoardStateEvaluator(checkedEvaluator, checkMatedEvaluator);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isEmpty());
    }

    @Test
    void testCheckedBoardState() {
        var board = mock(Board.class);

        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(new CheckedBoardState(board, color));
            });

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);

        var evaluator = new CheckableBoardStateEvaluator(checkedEvaluator, checkMatedEvaluator);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isPresent());
        assertEquals(BoardState.Type.CHECKED, boardState.get().getType());
    }

    @Test
    void testCheckMatedBoardState() {
        var board = mock(Board.class);

        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(new CheckedBoardState(board, color));
            });

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        when(checkMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(new CheckMatedBoardState(board, color));
            });


        var evaluator = new CheckableBoardStateEvaluator(checkedEvaluator, checkMatedEvaluator);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isPresent());
        assertTrue(boardState.get().isType(BoardState.Type.CHECK_MATED));
    }
}
