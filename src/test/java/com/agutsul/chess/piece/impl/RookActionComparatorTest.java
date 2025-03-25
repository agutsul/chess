package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isCastling;
import static com.agutsul.chess.activity.action.Action.isMove;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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

        assertTrue(isCapture(actions.getFirst()));
        assertTrue(isCastling(actions.getLast()));
        assertTrue(isMove(actions.get(1)));
    }
}