package com.agutsul.chess.rule.board;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.Executors;

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
        var executor = Executors.newSingleThreadExecutor();

        var board = mock(AbstractBoard.class);
        when(board.getExecutorService())
            .thenReturn(executor);

        var journal = mock(Journal.class);

        var boardStateEvaluator = new BoardStateEvaluatorImpl(board, journal);
        try {
            var boardState = boardStateEvaluator.evaluate(Colors.WHITE);
            assertEquals(BoardState.Type.STALE_MATED, boardState.getType());
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, MICROSECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }
}