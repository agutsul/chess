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

    @Test
    void testKingDoubleCheck() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e7")
                .withBlackRooks("a8","h8")
                .withBlackBishops("f8","f5")
                .withBlackKnights("b8","f6")
                .withBlackPawns("a5","b7","c5","h5")
                .withWhiteKing("g2")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","e1")
                .withWhiteKnights("b5","g6")
                .withWhiteBishop("g5")
                .withWhitePawns("a2","b2","c2","d3","f2","h2")
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