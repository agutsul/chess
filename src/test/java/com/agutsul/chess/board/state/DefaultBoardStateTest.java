package com.agutsul.chess.board.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StringBoardBuilder;

@ExtendWith(MockitoExtension.class)
public class DefaultBoardStateTest {

    @Test
    void testGetActions() {
        var board = new StringBoardBuilder()
                .withWhitePawn("a2")
                .build();

        assertTrue(board.getState() instanceof DefaultBoardState);

        var whitePawn = board.getPiece("a2").get();
        var pawnActions = board.getActions(whitePawn);

        assertFalse(pawnActions.isEmpty());
        assertEquals(2, pawnActions.size());
    }
}