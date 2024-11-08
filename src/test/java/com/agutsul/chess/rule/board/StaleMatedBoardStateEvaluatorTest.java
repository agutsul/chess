package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class StaleMatedBoardStateEvaluatorTest {

    @Test
    void testStaleMate() {
        var board = new BoardBuilder()
                .withWhiteQueen("g6")
                .withWhiteKing("a1")
                .withBlackKing("h8")
                .build();

        var evaluator = new StaleMatedBoardStateEvaluator(board);
        var boardState = evaluator.evaluate(Colors.BLACK);

        assertTrue(boardState.isPresent());
        assertEquals(BoardState.Type.STALE_MATED, boardState.get().getType());
    }
}
