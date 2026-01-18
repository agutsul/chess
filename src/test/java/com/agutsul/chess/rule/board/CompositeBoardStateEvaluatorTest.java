package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.fiftyMovesBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.fiveFoldRepetitionBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.seventyFiveMovesBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.threeFoldRepetitionBoardState;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
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
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class CompositeBoardStateEvaluatorTest {

    @AutoClose
    ExecutorService executorService = newSingleThreadExecutor();

    @Mock
    Board board;
    @Mock
    Piece<Color> piece;
    @Mock
    CheckedBoardStateEvaluator checkedEvaluator;
    @Mock
    CheckMatedBoardStateEvaluator checkMatedEvaluator;
    @Mock
    StaleMatedBoardStateEvaluator staleMatedEvaluator;
    @Mock
    FoldRepetitionBoardStateEvaluator foldRepetitionEvaluator;
    @Mock
    MovesBoardStateEvaluator movesEvaluator;
    @Mock
    InsufficientMaterialBoardStateEvaluator insufficientMaterialEvaluator;

    CompositeBoardStateEvaluator evaluator;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(board.getExecutorService())
            .thenReturn(executorService);

        evaluator = new CompositeBoardStateEvaluator(board,
                checkedEvaluator, checkMatedEvaluator, staleMatedEvaluator,
                foldRepetitionEvaluator, movesEvaluator, insufficientMaterialEvaluator
        );
    }

    @Test
    void evaluateCheckedBoardState() {
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkedBoardState(board, color, piece));
            });

        when(checkMatedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

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
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkedBoardState(board, color, piece));
            });

        when(checkMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(checkMatedBoardState(board, color, piece));
            });

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
        when(checkedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        when(staleMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                return Optional.of(staleMatedBoardState(board, color));
            });

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
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                var state = threeFoldRepetitionBoardState(board, actionMemento);
                return Optional.of(state);
            });

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
        when(movesEvaluator.evaluate(any()))
            .thenReturn(Optional.of(fiftyMovesBoardState(board, Colors.WHITE)));

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
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = checkedBoardState(board, color, piece);
                return Optional.of(state);
            });

        when(checkMatedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = checkMatedBoardState(board, color, piece);
                return Optional.of(state);
            });

        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                var state = threeFoldRepetitionBoardState(board, actionMemento);
                return Optional.of(state);            });

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
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = checkedBoardState(board, color, piece);
                return Optional.of(state);
            });

        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                var state = threeFoldRepetitionBoardState(board, actionMemento);
                return Optional.of(state);
            });

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertFalse(boardStates.isEmpty());

        var boardState = boardStates.getFirst();
        assertEquals(Colors.WHITE, boardState.getColor());
    }

    @Test
    void evaluateMultipleNonTerminalBoardStates() {
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                var state = threeFoldRepetitionBoardState(board, actionMemento);
                return Optional.of(state);
            });

        when(movesEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = fiftyMovesBoardState(board, color);
                return Optional.of(state);
            });

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
        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                var state = fiveFoldRepetitionBoardState(board, actionMemento);
                return Optional.of(state);
            });

        when(movesEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = seventyFiveMovesBoardState(board, color);
                return Optional.of(state);
            });

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
        when(checkedEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = checkedBoardState(board, color, piece);
                return Optional.of(state);
            });

        when(foldRepetitionEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                var actionMemento = mock(ActionMemento.class);
                when(actionMemento.getColor())
                    .thenReturn(color);

                var state = fiveFoldRepetitionBoardState(board, actionMemento);
                return Optional.of(state);
            });

        when(movesEvaluator.evaluate(any()))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);
                var state = seventyFiveMovesBoardState(board, color);
                return Optional.of(state);
            });

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
    void evaluateEmptyBoardState() {
        when(checkedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        when(staleMatedEvaluator.evaluate(any()))
            .thenReturn(Optional.empty());

        var boardStates = evaluator.evaluate(Colors.WHITE);
        assertTrue(boardStates.isEmpty());
    }
}