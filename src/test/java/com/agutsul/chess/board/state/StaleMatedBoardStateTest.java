package com.agutsul.chess.board.state;

import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class StaleMatedBoardStateTest {

    @Test
    void testGetActions() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        board.setState(staleMatedBoardState(board, Colors.WHITE));

        var whitePawn = board.getPiece("a2").get();
        assertTrue(board.getActions(whitePawn).isEmpty());
    }

    @Test
    void testGetImpacts() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        board.setState(staleMatedBoardState(board, Colors.WHITE));

        var whitePawn = board.getPiece("a2").get();
        assertTrue(board.getImpacts(whitePawn).isEmpty());
    }
}