package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.event.SetActionCounterEvent;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class MovesBoardStateEvaluator2Test {

    @Mock
    StandardBoard board;

    @Test
    void testMovesBoardStateEvaluatorWithEmptyJournal() {
        var journal = mock(Journal.class);
        when(journal.size(any()))
            .thenReturn(0);

        @SuppressWarnings("unchecked")
        var evaluator = new MovesBoardStateEvaluator(board, journal);
        var result = evaluator.evaluate(Colors.WHITE);

        assertTrue(result.isEmpty());
    }

    @Test
    void testMovesBoardStateEvaluatorWithHalfMovesMatch() {
        var board = new StandardBoard();

        var journal = mock(Journal.class);
        when(journal.size(any()))
            .thenReturn(50);

        @SuppressWarnings("unchecked")
        var evaluator = new MovesBoardStateEvaluator(board, journal);

        board.notifyObservers(new SetActionCounterEvent(50));

        var result = evaluator.evaluate(Colors.WHITE);
        var boardState = result.get();

        assertEquals(BoardState.Type.FIFTY_MOVES, boardState.getType());
    }

    @Test
    void testMovesBoardStateEvaluatorWithHalfMovesNotMatch() {
        var board = new StandardBoard();

        var journal = mock(Journal.class);
        when(journal.size(any()))
            .thenReturn(50);

        @SuppressWarnings("unchecked")
        var evaluator = new MovesBoardStateEvaluator(board, journal);

        board.notifyObservers(new SetActionCounterEvent(49));

        var result = evaluator.evaluate(Colors.WHITE);
        assertTrue(result.isEmpty());
    }
}