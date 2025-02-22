package com.agutsul.chess.board.state;
import static com.agutsul.chess.board.state.BoardStateFactory.seventyFiveMovesBoardState;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class SeventyFiveMovesBoardStateTest {

    @Test
    void testGetActions() {
        var board = new BoardBuilder()
                .withWhitePawn("a2")
                .build();

        board.setState(seventyFiveMovesBoardState(board, Colors.WHITE));

        var whitePawn = board.getPiece("a2").get();
        assertTrue(board.getActions(whitePawn).isEmpty());
    }

    @Test
    void testGetImpacts() {
        var board = new BoardBuilder()
                .withWhitePawn("a2")
                .build();

        board.setState(seventyFiveMovesBoardState(board, Colors.WHITE));

        var whitePawn = board.getPiece("a2").get();
        assertTrue(board.getImpacts(whitePawn).isEmpty());
    }
}