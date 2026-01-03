package com.agutsul.chess.antlr.pgn.action;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.UnknownPieceException;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class PawnPromoteCaptureActionAdapterTest {

    @Mock
    Board board;

    @ParameterizedTest(name = "{index}. testAdaptInvalidPawnPromoteCaptureAction({0})")
    @ValueSource(strings = { "zx9Q", "ax0R", "2xaQ", "A2r1", "Aa22", "a11", "cxb3Q", "fxe8" })
    void testAdaptInvalidPawnPromoteCaptureAction(String action) {
        var adapter = new PawnPromoteCaptureActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt(action)
        );

        var expectedMessage = String.format("Invalid action format: '%s'", action);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    void testAdaptUnknownPawnPromoteCaptureAction() {
        when(board.getPieces(any(Color.class), any(Piece.Type.class)))
            .thenReturn(emptyList());

        var adapter = new PawnPromoteCaptureActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                UnknownPieceException.class,
                () -> adapter.adapt("fxe8R")
        );

        assertEquals("Unknown source piece for action: 'fxe8R'", thrown.getMessage());
    }

    @Test
    void testAdaptPiecePromoteCaptureAction() {
        var promotionBoard = new LabeledBoardBuilder()
                .withWhitePawn("e7")
                .withBlackKnight("f8")
                .build();

        var adapter = new PawnPromoteCaptureActionAdapter(promotionBoard, Colors.WHITE);
        var action = adapter.adapt("exf8Q");

        assertEquals("e7 f8", action);
    }
}