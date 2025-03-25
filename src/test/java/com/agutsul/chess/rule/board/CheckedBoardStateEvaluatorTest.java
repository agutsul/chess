package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CheckedBoardStateEvaluatorTest {

    @Test
    void testKingCheck() {
        var board = new LabeledBoardBuilder()
                .withBlackRook("g8")
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteQueen("c3")
                .build();

        assertChecked(board, Colors.BLACK);
    }

    @Test
    void testIsChecked() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withWhiteQueen("a1")
                .build();

        assertChecked(board, Colors.BLACK);
    }

    private static void assertChecked(Board board, Color color) {
        var checkEvaluator = new CheckedBoardStateEvaluator(board);
        var boardState = checkEvaluator.evaluate(color);

        assertTrue(boardState.isPresent());
        assertEquals(BoardState.Type.CHECKED, boardState.get().getType());
    }
}