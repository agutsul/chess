package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.CancelMoveAction;
import com.agutsul.chess.board.BoardBuilder;

@ExtendWith(MockitoExtension.class)
public class CancelMoveActionTest {

    @Test
    void testCancelMoveAction() {
        var board = new BoardBuilder()
                .withWhitePawn("e2")
                .build();

        var whitePawn = board.getPiece("e2").get();
        var actions = board.getActions(whitePawn);

        var sourcePosition = whitePawn.getPosition();
        var targetPosition = board.getPosition("e4").get();

        var moveAction = actions.stream()
                .filter(action -> Action.Type.MOVE.equals(action.getType()))
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        moveAction.get().execute();

        @SuppressWarnings({ "rawtypes", "unchecked" })
        var cancelAction = new CancelMoveAction(whitePawn, sourcePosition);
        cancelAction.execute();

        assertEquals(sourcePosition, whitePawn.getPosition());
    }
}