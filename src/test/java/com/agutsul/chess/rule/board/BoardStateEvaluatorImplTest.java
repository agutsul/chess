package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class BoardStateEvaluatorImplTest {

    @Test
    @SuppressWarnings("unchecked")
    void testBoardStateEvaluatorImpl() {
        var board = mock(AbstractBoard.class);
        var journal = mock(Journal.class);

        var boardStateEvaluator = new BoardStateEvaluatorImpl(board, journal);
        var boardState = boardStateEvaluator.evaluate(Colors.WHITE);
        assertEquals(BoardState.Type.STALE_MATED, boardState.getType());

        verify(board, times(1)).addObserver(any());
    }
}