package com.agutsul.chess.pgn.action;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;

@ExtendWith(MockitoExtension.class)
public class PieceCaptureActionAdapterTest {

    @Mock
    Board board;

    @Test
    void testAdaptInvalidPieceCaptureAction() {
        var adapter = new PieceCaptureActionAdapter(board, Colors.WHITE);

        for (var action : List.of("Zxz9", "Xxa0", "1x2a", "A2x1", "Aaa1", "a11a", "cxb31", "ex4e4")) {
            var thrown = assertThrows(
                    IllegalActionException.class,
                    () -> adapter.adapt(action)
            );

            var expectedMessage = String.format("Invalid action format: '%s'", action);
            assertEquals(expectedMessage, thrown.getMessage());
        }
    }

    @Test
    void testAdaptUnknownPieceCaptureAction() {
        when(board.getPieces(any(), any()))
            .thenReturn(emptyList());

        var adapter = new PieceCaptureActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt("Nxe4")
        );

        assertEquals("Unknown source piece for action: 'Nxe4'", thrown.getMessage());
    }

    @Test
    void testAdaptPieceCaptureAction() {
        var captureBoard = new BoardBuilder()
                .withWhiteBishop("e5")
                .withBlackKnight("d6")
                .build();

        var adapter = new PieceCaptureActionAdapter(captureBoard, Colors.WHITE);
        var action = adapter.adapt("Bxd6");

        assertEquals("e5 d6", action);
    }
}