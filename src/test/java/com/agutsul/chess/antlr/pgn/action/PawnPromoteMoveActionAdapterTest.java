package com.agutsul.chess.antlr.pgn.action;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
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
public class PawnPromoteMoveActionAdapterTest {

    @Mock
    Board board;

    @DisplayName("testAdaptInvalidPawnPromoteMoveAction")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "z9Q", "a0R", "2aQ", "A2r", "Aa22", "a11", "cxb3", "e4e4" })
    void testAdaptInvalidPawnPromoteMoveAction(String action) {
        var adapter = new PawnPromoteMoveActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt(action)
        );

        var expectedMessage = String.format("Invalid action format: '%s'", action);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    void testAdaptUnknownPawnPromoteMoveAction() {
        when(board.getPieces(any(Color.class), any(Piece.Type.class)))
            .thenReturn(emptyList());

        var adapter = new PawnPromoteMoveActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                UnknownPieceException.class,
                () -> adapter.adapt("e8R")
        );

        assertEquals("Unknown source piece for action: 'e8R'", thrown.getMessage());
    }

    @Test
    void testAdaptPiecePromoteMoveAction() {
        var promotionBoard = new LabeledBoardBuilder().withWhitePawn("e7").build();

        var adapter = new PawnPromoteMoveActionAdapter(promotionBoard, Colors.WHITE);
        var action = adapter.adapt("e8Q");

        assertEquals("e7 e8", action);
    }
}