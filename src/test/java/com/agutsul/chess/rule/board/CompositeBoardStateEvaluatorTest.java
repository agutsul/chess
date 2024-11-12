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
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.board.state.FiftyMovesBoardState;
import com.agutsul.chess.board.state.StaleMatedBoardState;
import com.agutsul.chess.board.state.ThreeFoldRepetitionBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CompositeBoardStateEvaluatorTest {

    @Test
    void evaluateCheckedBoardState() {
        var board = mock(Board.class);

        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(new CheckedBoardState(board, color));
            });

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        when(checkMatedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);

        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator);

        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState instanceof CheckedBoardState);
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateCheckMatedBoardState() {
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

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);

        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator);

        var boardState = evaluator.evaluate(Colors.BLACK);

        assertTrue(boardState instanceof CheckMatedBoardState);
        assertEquals(Colors.BLACK, boardState.getColor());
    }

    @Test
    void evaluateStaleMatedBoardState() {
        var board = mock(Board.class);

        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        when(staleMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(new StaleMatedBoardState(board, color));
            });

        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator);

        var boardState = evaluator.evaluate(Colors.BLACK);

        assertTrue(boardState instanceof StaleMatedBoardState);
        assertEquals(Colors.BLACK, boardState.getColor());
    }

    @Test
    void evaluateFoldRepetitionBoardState() {
        var board = mock(Board.class);

        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);

        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenReturn(Optional.of(new ThreeFoldRepetitionBoardState(board, Colors.WHITE)));

        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator);

        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState instanceof ThreeFoldRepetitionBoardState);
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateMovesBoardState() {
        var board = mock(Board.class);

        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);

        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        when(movesEvaluator.evaluate(any()))
            .thenReturn(Optional.of(new FiftyMovesBoardState(board, Colors.WHITE)));

        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator);

        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState instanceof FiftyMovesBoardState);
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateDefaultBoardState() {
        var board = mock(Board.class);

        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        when(staleMatedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator);

        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState instanceof DefaultBoardState);
        assertEquals(Colors.WHITE, boardState.getColor());
    }
}