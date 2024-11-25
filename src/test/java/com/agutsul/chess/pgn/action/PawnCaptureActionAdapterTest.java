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
import com.agutsul.chess.exception.UnknownPieceException;

@ExtendWith(MockitoExtension.class)
public class PawnCaptureActionAdapterTest {

    @Mock
    Board board;

    @Test
    void testAdaptInvalidPawnCaptureAction() {
        var adapter = new PawnCaptureActionAdapter(board, Colors.WHITE);

        for (var action : List.of("zxz9", "xxa0", "1x2a", "A2x1", "Aaa1", "a11a", "cxb31", "ex4e4")) {
            var thrown = assertThrows(
                    IllegalActionException.class,
                    () -> adapter.adapt(action)
            );

            var expectedMessage = String.format("Invalid action format: '%s'", action);
            assertEquals(expectedMessage, thrown.getMessage());
        }
    }

    @Test
    void testAdaptUnknownPawnCaptureAction() {
        when(board.getPieces(any(), any()))
            .thenReturn(emptyList());

        var adapter = new PawnCaptureActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                UnknownPieceException.class,
                () -> adapter.adapt("dxe4")
        );

        assertEquals("Unknown source piece for action: 'dxe4'", thrown.getMessage());
    }

    @Test
    void testAdaptPawnCaptureAction() {
        var captureBoard = new BoardBuilder()
                .withWhitePawn("e5")
                .withBlackKnight("d6")
                .build();

        var adapter = new PawnCaptureActionAdapter(captureBoard, Colors.WHITE);
        var action = adapter.adapt("exd6");

        assertEquals("e5 d6", action);
    }
}