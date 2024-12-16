package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.BoardBuilder;

@ExtendWith(MockitoExtension.class)
public class PieceMoveActionTest {

    @Test
    void testWhiteKnightMoveAction() {
        var board = new BoardBuilder()
                .withWhiteKnight("e5")
                .build();

        var whiteKnight = board.getPiece("e5").get();
        var actions = board.getActions(whiteKnight);
        assertEquals(8, actions.size());

        var sourcePosition = whiteKnight.getPosition();
        var targetPosition = board.getPosition("c6").get();

        var moveAction = actions.stream()
                .filter(action -> Action.Type.MOVE.equals(action.getType()))
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        assertTrue(moveAction.isPresent());
        assertEquals("Ne5->c6", moveAction.get().getCode());

        moveAction.get().execute();

        assertEquals(targetPosition, whiteKnight.getPosition());
        assertTrue(board.isEmpty(sourcePosition));
    }
}