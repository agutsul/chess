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

@ExtendWith(MockitoExtension.class)
public class InsufficientMaterialBoardStateEvaluatorTest {

    @Test
    // https://en.wikipedia.org/wiki/Draw_(chess)
    void testVidmarVsMaroczy1932() {
        var board = new StringBoardBuilder()
                .withWhiteKing("g4")
                .withBlackKing("f7")
                .withBlackBishop("c7")
                .build();

        assertInsufficientMaterial(board, Colors.BLACK);
    }

    // https://chess.fandom.com/wiki/Dead_Position

    @Test
    void testKingVsKing() {
        var board = new StringBoardBuilder()
                .withWhiteKing("e3")
                .withBlackKing("e5")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    void testKingAndBishopVsKing() {
        var board = new StringBoardBuilder()
                .withBlackKing("f7")
                .withWhiteKing("e5")
                .withWhiteBishop("c3")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    void testKingAndKnightVsKing() {
        var board = new StringBoardBuilder()
                .withBlackKing("f7")
                .withWhiteKing("e5")
                .withWhiteKnight("c3")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    void testKingAndBishopsVsKing() {
        var board = new StringBoardBuilder()
                .withWhiteKing("f6")
                .withBlackKing("c3")
                .withBlackBishops("c2","c4")
                .build();

        assertInsufficientMaterial(board, Colors.BLACK);
    }

    @Test
    void testKingAndBishopVsKingAndBishop() {
        var board = new StringBoardBuilder()
                .withWhiteKing("g1")
                .withWhiteBishop("h2")
                .withBlackKing("f3")
                .withBlackBishop("e4")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    // https://support.chess.com/en/articles/8705277-what-does-insufficient-mating-material-mean

    @Test
    void testKingAndBishopVsKingAndKnight() {
        var board = new StringBoardBuilder()
                .withWhiteKing("d6")
                .withWhiteKnight("c5")
                .withBlackKing("b6")
                .withBlackBishop("a8")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    void testKingAndDoubleKnightsVsKing() {
        var board = new StringBoardBuilder()
                .withWhiteKing("g3")
                .withWhiteKnights("f4","f5")
                .withBlackKing("c1")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    private static void assertInsufficientMaterial(Board board, Color color) {
        var evaluator = new InsufficientMaterialBoardStateEvaluator(board);
        var boardState = evaluator.evaluate(color);

        assertTrue(boardState.isPresent());
        assertEquals(BoardState.Type.INSUFFICIENT_MATERIAL, boardState.get().getType());
    }
}