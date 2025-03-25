package com.agutsul.chess.antlr.pgn.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;

@ExtendWith(MockitoExtension.class)
public class PieceActionAdapterTest {

    @Test
    void testAdaptKingSideCastlingAction() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("h8")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("O-O");

        assertEquals("e8 g8", action);
    }

    @Test
    void testAdaptQueenSideCastlingAction() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("a8")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("O-O-O");

        assertEquals("e8 c8", action);
    }

    @Test
    void testAdaptInvalidAction() {
        var adapter = new PieceActionAdapter(mock(Board.class), Colors.BLACK);
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt("O")
        );

        assertEquals("Invalid action format: 'O'", thrown.getMessage());
    }

    @Test
    void testAdaptPawnMoveAction() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("e7")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("e5");

        assertEquals("e7 e5", action);
    }

    @Test
    void testAdaptPieceMoveAction() {
        var board = new LabeledBoardBuilder()
                .withBlackBishop("a6")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("Bb5");

        assertEquals("a6 b5", action);
    }

    @Test
    void testAdaptPawnPromoteMoveAction() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("a2")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("a1Q");

        assertEquals("a2 a1", action);
    }

    @Test
    void testAdaptPawnCaptureAction() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("a5")
                .withWhitePawn("b4")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("axb4");

        assertEquals("a5 b4", action);
    }

    @Test
    void testAdaptPieceCaptureAction() {
        var board = new LabeledBoardBuilder()
                .withBlackKnight("e5")
                .withWhitePawn("f3")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("Nxf3");

        assertEquals("e5 f3", action);
    }

    @Test
    void testAdaptPawnPromoteCaptureAction() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("a2")
                .withWhiteKnight("b1")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("axb1Q");

        assertEquals("a2 b1", action);
    }

    @Test
    void testAdaptInvalidPawnPromoteCaptureAction() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("a2")
                .withWhiteKnight("b1")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt("axb1q")
        );

        assertEquals("Invalid action format: 'axb1q'", thrown.getMessage());
    }

    @Test
    void testAdaptInvalidPawnPromoteMoveAction() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("b2")
                .build();

        var adapter = new PieceActionAdapter(board, Colors.BLACK);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt("b1q")
        );

        assertEquals("Invalid action format: 'b1q'", thrown.getMessage());
    }
}