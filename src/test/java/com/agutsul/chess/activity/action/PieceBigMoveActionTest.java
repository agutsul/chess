package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;

@ExtendWith(MockitoExtension.class)
public class PieceBigMoveActionTest {

    @Test
    void testWhitePawnBigMoveAction() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e2")
                .build();

        var whitePawn = board.getPiece("e2").get();
        var actions = board.getActions(whitePawn);
        assertEquals(2, actions.size());

        var sourcePosition = whitePawn.getPosition();
        var targetPosition = board.getPosition("e4").get();

        var moveAction = actions.stream()
                .filter(action -> Action.Type.BIG_MOVE.equals(action.getType()))
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        assertTrue(moveAction.isPresent());
        assertEquals("e2->e4", moveAction.get().getCode());

        moveAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(targetPosition, whitePawn.getPosition());
        assertTrue(board.isEmpty(sourcePosition));
    }
}