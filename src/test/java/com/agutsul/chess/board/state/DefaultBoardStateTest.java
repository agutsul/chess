package com.agutsul.chess.board.state;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.state.BoardStateFactory.DefaultBoardState;

@ExtendWith(MockitoExtension.class)
public class DefaultBoardStateTest {

    @Test
    void testGetActions() {
        var board = new BoardBuilder()
                .withWhitePawn("a2")
                .build();

        assertEquals(board.getState().getClass(), DefaultBoardState.class);

        var whitePawn = board.getPiece("a2").get();
        var pawnActions = board.getActions(whitePawn);

        assertFalse(pawnActions.isEmpty());
        assertEquals(2, pawnActions.size());
    }
}
