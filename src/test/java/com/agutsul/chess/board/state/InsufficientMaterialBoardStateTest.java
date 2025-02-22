package com.agutsul.chess.board.state;
import static com.agutsul.chess.board.state.BoardStateFactory.insufficientMaterialBoardState;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class InsufficientMaterialBoardStateTest {

    @Test
    void testGetActions() {
        var board = new BoardBuilder()
                .withWhitePawn("a2")
                .build();

        board.setState(insufficientMaterialBoardState(board, Colors.WHITE));

        var whitePawn = board.getPiece("a2").get();
        assertFalse(board.getActions(whitePawn).isEmpty());
    }

    @Test
    void testGetImpacts() {
        var board = new BoardBuilder()
                .withWhitePawn("a2")
                .build();

        board.setState(insufficientMaterialBoardState(board, Colors.WHITE));

        var whitePawn = board.getPiece("a2").get();
        assertFalse(board.getImpacts(whitePawn).isEmpty());
    }
}