package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.event.SetActionCounterEvent;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.FiftyMovesBoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class MovesBoardStateEvaluator2Test {

    @Mock
    StandardBoard board;
    @Mock
    Journal<ActionMemento<?,?>> journal;

    @InjectMocks
    MovesBoardStateEvaluator evaluator;

    @Test
    void testMovesBoardStateEvaluatorWithEmptyJournal() {
        when(journal.size(any()))
            .thenReturn(0);

        var result = evaluator.evaluate(Colors.WHITE);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMovesBoardStateEvaluatorWithHalfMovesMatch() {
        var board = new StandardBoard();

        when(journal.size(any()))
            .thenReturn(FiftyMovesBoardState.MOVES);

        var evaluator = new MovesBoardStateEvaluator(board, journal);

        board.notifyObservers(new SetActionCounterEvent(FiftyMovesBoardState.MOVES));

        var result = evaluator.evaluate(Colors.WHITE);
        var boardState = result.get();

        assertEquals(BoardState.Type.FIFTY_MOVES, boardState.getType());
    }

    @Test
    void testMovesBoardStateEvaluatorWithHalfMovesNotMatch() {
        var board = new StandardBoard();

        when(journal.size(any()))
            .thenReturn(FiftyMovesBoardState.MOVES);

        var evaluator = new MovesBoardStateEvaluator(board, journal);

        board.notifyObservers(new SetActionCounterEvent(49));

        var result = evaluator.evaluate(Colors.WHITE);
        assertTrue(result.isEmpty());
    }
}