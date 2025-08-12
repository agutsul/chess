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
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.UnknownPieceException;

@ExtendWith(MockitoExtension.class)
public class PieceMoveActionAdapterTest {

    @Mock
    Board board;

    @ParameterizedTest(name = "{index}. testAdaptInvalidPieceMoveAction({0})")
    @ValueSource(strings = { "Wz9", "Ba0", "Q12a", "Ac2", "Aa2", "Ba11", "cxb3" })
    void testAdaptInvalidPieceMoveAction(String action) {
        var adapter = new PieceMoveActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt(action)
        );

        var expectedMessage = String.format("Invalid action format: '%s'", action);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    void testAdaptUnknownPieceMoveAction() {
        when(board.getPieces(any(), any()))
            .thenReturn(emptyList());

        var adapter = new PieceMoveActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                UnknownPieceException.class,
                () -> adapter.adapt("Nf3")
        );

        assertEquals("Unknown source piece for action: 'Nf3'", thrown.getMessage());
    }

    @Test
    void testAdaptPieceMoveAction() {
        var adapter = new PieceMoveActionAdapter(new StandardBoard(), Colors.WHITE);
        var action = adapter.adapt("Nf3");

        assertEquals("g1 f3", action);
    }
}