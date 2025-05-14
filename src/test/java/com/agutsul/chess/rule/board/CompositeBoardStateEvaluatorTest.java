package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.fiftyMovesBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.fiveFoldRepetitionBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.seventyFiveMovesBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.threeFoldRepetitionBoardState;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CompositeBoardStateEvaluatorTest {

    @AutoClose
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Mock
    Board board;

    @BeforeEach
    void setUp() {
        when(board.getExecutorService())
            .thenReturn(executorService);
    }

    @Test
    void evaluateCheckedBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkedBoardState(board, color));
            });

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        when(checkMatedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.CHECKED));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateCheckMatedBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkedBoardState(board, color));
            });

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        when(checkMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkMatedBoardState(board, color));
            });

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.BLACK);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.CHECK_MATED));
        assertTrue(boardStateMap.containsKey(BoardState.Type.CHECKED));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.BLACK, boardState.getColor());
    }

    @Test
    void evaluateStaleMatedBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        when(staleMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(staleMatedBoardState(board, color));
            });

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.BLACK);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.STALE_MATED));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.BLACK, boardState.getColor());
    }

    @Test
    void evaluateFoldRepetitionBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                return Optional.of(threeFoldRepetitionBoardState(board, actionMemento));
            });

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.THREE_FOLD_REPETITION));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateMovesBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        when(movesEvaluator.evaluate(any()))
            .thenReturn(Optional.of(fiftyMovesBoardState(board, Colors.WHITE)));

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.FIFTY_MOVES));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateCheckMatedAndFoldRepetitionBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = checkedBoardState(board, color);
                return Optional.of(state);
            });

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        when(checkMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = checkMatedBoardState(board, color);
                return Optional.of(state);
            });

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);

        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                return Optional.of(threeFoldRepetitionBoardState(board, actionMemento));
            });

        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.CHECK_MATED));
        assertTrue(boardStateMap.containsKey(BoardState.Type.CHECKED));
        assertTrue(boardStateMap.containsKey(BoardState.Type.THREE_FOLD_REPETITION));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateCheckedAndFoldRepetitionBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = checkedBoardState(board, color);
                return Optional.of(state);
            });

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);

        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                return Optional.of(threeFoldRepetitionBoardState(board, actionMemento));
            });

        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateMultipleNonTerminalBoardStates() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                return Optional.of(threeFoldRepetitionBoardState(board, actionMemento));
            });

        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        when(movesEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = fiftyMovesBoardState(board, color);
                return Optional.of(state);
            });

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.THREE_FOLD_REPETITION));
        assertTrue(boardStateMap.containsKey(BoardState.Type.FIFTY_MOVES));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateMultipleTerminalStates() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                return Optional.of(fiveFoldRepetitionBoardState(board, actionMemento));
            });

        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        when(movesEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = seventyFiveMovesBoardState(board, color);
                return Optional.of(state);
            });

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.SEVENTY_FIVE_MOVES));
        assertTrue(boardStateMap.containsKey(BoardState.Type.FIVE_FOLD_REPETITION));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateMultipleTerminalStatesAndCheckedState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = checkedBoardState(board, color);
                return Optional.of(state);
            });

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                return Optional.of(fiveFoldRepetitionBoardState(board, actionMemento));
            });

        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        when(movesEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = seventyFiveMovesBoardState(board, color);
                return Optional.of(state);
            });

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardStateMap = boardStates.stream()
                .collect(toMap(BoardState::getType, identity()));

        assertTrue(boardStateMap.containsKey(BoardState.Type.FIVE_FOLD_REPETITION));
        assertTrue(boardStateMap.containsKey(BoardState.Type.CHECKED));
        assertTrue(boardStateMap.containsKey(BoardState.Type.SEVENTY_FIVE_MOVES));

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateDefaultBoardState() {
        var checkedEvaluator = mock(CheckedBoardStateEvaluator.class);
        when(checkedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var checkMatedEvaluator = mock(CheckMatedBoardStateEvaluator.class);
        var foldRepetitionEvaluator = mock(FoldRepetitionBoardStateEvaluator.class);
        var movesEvaluator = mock(MovesBoardStateEvaluator.class);
        var insufficientMaterialEvaluator = mock(InsufficientMaterialBoardStateEvaluator.class);

        var staleMatedEvaluator = mock(StaleMatedBoardStateEvaluator.class);
        when(staleMatedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        var evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertTrue(boardStates.isEmpty());

//        var boardState = boardStates.getFirst();
//        assertTrue(boardState instanceof DefaultBoardState);
//        assertEquals(Colors.WHITE, boardState.getColor());
    }
}