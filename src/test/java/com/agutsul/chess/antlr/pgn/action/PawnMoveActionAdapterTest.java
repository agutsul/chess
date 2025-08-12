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
public class PawnMoveActionAdapterTest {

    @Mock
    Board board;

    @ParameterizedTest(name = "{index}. testAdaptInvalidPawnMoveAction({0})")
    @ValueSource(strings = { "z9", "a0", "2a", "A2", "Aa", "a11", "cxb3", "e4e4" })
    void testAdaptInvalidPawnMoveAction(String action) {
        var adapter = new PawnMoveActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt(action)
        );

        var expectedMessage = String.format("Invalid action format: '%s'", action);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    void testAdaptUnknownPawnMoveAction() {
        when(board.getPieces(any(), any()))
            .thenReturn(emptyList());

        var adapter = new PawnMoveActionAdapter(board, Colors.WHITE);

        var thrown = assertThrows(
                UnknownPieceException.class,
                () -> adapter.adapt("e4")
        );

        assertEquals("Unknown source piece for action: 'e4'", thrown.getMessage());
    }

    @Test
    void testAdaptPawnMoveAction() {
        var adapter = new PawnMoveActionAdapter(new StandardBoard(), Colors.WHITE);
        var action = adapter.adapt("e4");

        assertEquals("e2 e4", action);
    }
}