package com.agutsul.chess.antlr.pgn.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;

@ExtendWith(MockitoExtension.class)
public class PawnPromotionTypeAdapterTest {

    @Mock
    Board board;

    @DisplayName("testAdaptInvalidPawnPromotionTypeAction")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "z9Q", "a0R", "2aQ", "A2r", "Aa22", "a11", "cxb3", "e4e4" })
    void testAdaptInvalidPawnPromotionTypeAction(String action) {
        var adapter = new PawnPromotionTypeAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt(action)
        );

        var expectedMessage = String.format("Invalid action format: '%s'", action);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    void testAdaptPawnPromotionTypeMoveAction() {
        var promotionBoard = new LabeledBoardBuilder().withWhitePawn("b7").build();

        var adapter = new PawnPromotionTypeAdapter(promotionBoard, Colors.WHITE);
        var action = adapter.adapt("b8Q");

        assertEquals("Q", action);
    }

    @Test
    void testAdaptPiecePromoteCaptureAction() {
        var promotionBoard = new LabeledBoardBuilder()
                .withWhitePawn("e7")
                .withBlackKnight("f8")
                .build();

        var adapter = new PawnPromotionTypeAdapter(promotionBoard, Colors.WHITE);
        var action = adapter.adapt("exf8R");

        assertEquals("R", action);
    }
}