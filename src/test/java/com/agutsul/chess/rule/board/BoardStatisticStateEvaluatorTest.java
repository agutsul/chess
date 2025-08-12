package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;
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
public class BoardStatisticStateEvaluatorTest {

    @Mock
    BoardState boardState;
    @Mock
    BoardStateEvaluator<Optional<BoardState>> eval;

    @InjectMocks
    BoardStatisticStateEvaluator evaluator;

    @Test
    void testTerminalBoardStateForRequstedColor() {
        when(boardState.isTerminal())
            .thenReturn(true);

        when(eval.evaluate(any(Color.class)))
            .thenReturn(Optional.of(boardState));

        var result = evaluator.evaluate(Colors.WHITE);

        assertTrue(result.isPresent());
        assertEquals(boardState, result.get());

        verify(eval, times(1)).evaluate(any());
    }

    @Test
    void testBoardStateForOpponentColor() {
        when(eval.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.ofNullable(Objects.equals(Colors.WHITE, color)
                        ? null
                        : defaultBoardState(mock(Board.class), color)
                );
            });

        var result = evaluator.evaluate(Colors.WHITE);

        assertTrue(result.isPresent());
        assertEquals(Colors.WHITE, result.get().getColor());

        verify(eval, times(2)).evaluate(any());
    }

    @Test
    void testNonTerminalBoardStateForRequstedColor() {
        when(boardState.isTerminal())
            .thenReturn(false);

        when(eval.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.ofNullable(Objects.equals(Colors.WHITE, color)
                        ? boardState
                        : null
                );
            });

        var result = evaluator.evaluate(Colors.WHITE);

        assertTrue(result.isPresent());
        assertEquals(boardState, result.get());

        verify(eval, times(2)).evaluate(any());
    }

    @Test
    void testNonTerminalBoardStateForOpponentColor() {
        var whiteBoardState = mock(BoardState.class);
        when(whiteBoardState.isTerminal())
            .thenReturn(false);

        var blackBoardState = mock(BoardState.class);
        when(blackBoardState.isTerminal())
            .thenReturn(false);

        when(eval.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(Objects.equals(Colors.WHITE, color)
                        ? whiteBoardState
                        : blackBoardState
                );
            });

        var result = evaluator.evaluate(Colors.WHITE);

        assertTrue(result.isPresent());
        assertEquals(whiteBoardState, result.get());

        verify(eval, times(2)).evaluate(any());
    }

    @Test
    void testTerminalBoardStateForOpponentColor() {
        var whiteBoardState = mock(BoardState.class);
        when(whiteBoardState.isTerminal())
            .thenReturn(false);

        var blackBoardState = mock(BoardState.class);
        when(blackBoardState.getColor())
            .thenReturn(Colors.BLACK);
        when(blackBoardState.isTerminal())
            .thenReturn(true);

        when(eval.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(Objects.equals(Colors.WHITE, color)
                        ? whiteBoardState
                        : blackBoardState
                );
            });

        var result = evaluator.evaluate(Colors.WHITE);
        assertTrue(result.isPresent());

        var resultState = result.get();
        assertEquals(Colors.WHITE, resultState.getColor());
        assertNotEquals(whiteBoardState, resultState);
        assertNotEquals(blackBoardState, resultState);

        verify(eval, times(2)).evaluate(any());
    }
}