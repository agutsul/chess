package com.agutsul.chess.piece.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.StringBoardBuilder;

@ExtendWith(MockitoExtension.class)
public class RookActionComparatorTest {

    @Test
    void testRookActionsSorting() {
        var board = new StringBoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .withBlackPawn("h5")
                .build();

        var rook = board.getPiece("h1").get();
        var actions = new ArrayList<>(board.getActions(rook));

        assertFalse(actions.isEmpty());
        assertEquals(7, actions.size());

        assertEquals(Action.Type.CAPTURE,  actions.get(0).getType());
        assertEquals(Action.Type.CASTLING, actions.get(actions.size() - 1).getType());
        assertEquals(Action.Type.MOVE,     actions.get(1).getType());
    }
}