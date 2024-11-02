package com.agutsul.chess.board.state;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
// https://en.wikipedia.org/wiki/Stalemate
public class StaleMateTest {

    @Test
    void testStaleMate() {
        var board = new BoardBuilder()
                .withWhiteQueen("g6")
                .withWhiteKing("a1")
                .withBlackKing("h8")
                .build();

        assertTrue(board.isStaleMated(Colors.BLACK));
    }
}