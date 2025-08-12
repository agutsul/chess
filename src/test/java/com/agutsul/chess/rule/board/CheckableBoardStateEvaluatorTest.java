package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CheckableBoardStateEvaluatorTest {

    @Mock
    Board board;
    @Mock
    CheckedBoardStateEvaluator checkedEvaluator;
    @Mock
    CheckMatedBoardStateEvaluator checkMatedEvaluator;

    @InjectMocks
    CheckableBoardStateEvaluator evaluator;

    @Test
    void testNonCheckedBoardState() {
        when(checkedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isEmpty());
    }

    @Test
    void testCheckedBoardState() {
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkedBoardState(board, color));
            });

        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isPresent());
        assertEquals(BoardState.Type.CHECKED, boardState.get().getType());
    }

    @Test
    void testCheckMatedBoardState() {
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkedBoardState(board, color));
            });

        when(checkMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkMatedBoardState(board, color));
            });


        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isPresent());
        assertTrue(boardState.get().isType(BoardState.Type.CHECK_MATED));
    }
}