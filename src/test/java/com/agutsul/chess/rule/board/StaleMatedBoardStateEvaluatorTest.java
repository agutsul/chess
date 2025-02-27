package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

// https://en.wikipedia.org/wiki/Stalemate
@ExtendWith(MockitoExtension.class)
public class StaleMatedBoardStateEvaluatorTest {

    @Test
    void testStaleMate() {
        var board = new StringBoardBuilder()
                .withWhiteQueen("g6")
                .withWhiteKing("a1")
                .withBlackKing("h8")
                .build();

        assertStaleMate(board, Colors.BLACK);
    }

    @Test
    void testDiagram1StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("f8")
                .withWhiteKing("f6")
                .withWhitePawn("f7")
                .build();

        assertStaleMate(board, Colors.BLACK);
    }

    @Test
    void testDiagram2StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("a8")
                .withBlackBishop("b8")
                .withWhiteKing("b6")
                .withWhiteRook("h8")
                .build();

        assertStaleMate(board, Colors.BLACK);
    }

    @Test
    void testDiagram3StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("a1")
                .withBlackPawn("a2")
                .withWhiteKing("g5")
                .withWhiteQueen("b3")
                .build();

        assertStaleMate(board, Colors.BLACK);
    }

    @Test
    void testDiagram4StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("a8")
                .withWhiteKing("a6")
                .withWhitePawn("a7")
                .withWhiteBishop("f4")
                .build();

        assertStaleMate(board, Colors.BLACK);
    }

    @Test
    void testDiagram5StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("h1")
                .withBlackPawns("e2","g2","h2")
                .withBlackRook("g1")
                .withBlackBishop("f1")
                .withWhiteKing("e1")
                .build();

        assertStaleMate(board, Colors.BLACK);
    }

    @Test
    void testAnandVsKramnik2007StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("f5")
                .withBlackPawns("f6","g7")
                .withWhiteKing("h5")
                .withWhitePawn("h4")
                .build();

        assertStaleMate(board, Colors.WHITE);
    }

    @Test
    void testKorchnoiVsKarpov1978StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("h7")
                .withBlackPawn("a4")
                .withWhiteKing("f7")
                .withWhiteBishop("g7")
                .withWhitePawn("a3")
                .build();

        assertStaleMate(board, Colors.BLACK);
    }

    @Test
    void testBernsteinVsSmyslov1946StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("f5")
                .withBlackPawn("f4")
                .withBlackRook("b2")
                .withWhiteKing("f3")
                .build();

        assertStaleMate(board, Colors.WHITE);
    }

    @Test
    void testMatulovicVsMinev1956StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("h5")
                .withWhiteKing("h3")
                .withWhitePawn("f4")
                .withWhiteRook("a6")
                .build();

        assertStaleMate(board, Colors.BLACK);
    }

    @Test
    void testWilliamsVsHarrwitz1846StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("c4")
                .withBlackKnight("c3")
                .withBlackRook("b3")
                .withBlackPawn("a2")
                .withWhiteKing("a1")
                .build();

        assertStaleMate(board, Colors.WHITE);
    }

    @Test
    void testCarlsenVsVanWely2007StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("d3")
                .withBlackBishop("d2")
                .withBlackRook("f8")
                .withWhiteKing("d1")
                .build();

        assertStaleMate(board, Colors.WHITE);
    }

    @Test
    void testEvansVsReshevsky1963StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("g7")
                .withBlackQueen("g3")
                .withBlackRook("e2")
                .withBlackKnight("f4")
                .withBlackPawns("b5","e5","h5")
                .withWhiteKing("h1")
                .withWhitePawns("b4","e4","f3","h4")
                .build();

        assertStaleMate(board, Colors.WHITE);
    }

    @Test
    void testTroitskyVsVogt1896StaleMate() {
        var board = new StringBoardBuilder()
                .withBlackKing("d8")
                .withBlackQueen("d1")
                .withBlackRook("g6")
                .withBlackBishops("b6","h3")
                .withBlackPawns("a5","b7","c7","e5","f7","g7")
                .withWhiteKing("g1")
                .withWhiteRook("h1")
                .withWhiteKnight("g3")
                .withWhiteBishop("e1")
                .withWhitePawns("a4","b5","e4","f2","h2")
                .build();

        assertStaleMate(board, Colors.WHITE);
    }

    private static void assertStaleMate(Board board, Color color) {
        var evaluator = new StaleMatedBoardStateEvaluator(board);
        var boardState = evaluator.evaluate(color);

        assertTrue(boardState.isPresent());
        assertEquals(BoardState.Type.STALE_MATED, boardState.get().getType());
    }
}