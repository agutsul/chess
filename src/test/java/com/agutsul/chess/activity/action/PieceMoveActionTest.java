package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;

@ExtendWith(MockitoExtension.class)
public class PieceMoveActionTest {

    @Test
    void testWhiteKnightMoveAction() {
        var board = new LabeledBoardBuilder()
                .withWhiteKnight("e5")
                .build();

        var whiteKnight = board.getPiece("e5").get();
        var actions = board.getActions(whiteKnight);
        assertEquals(8, actions.size());

        var sourcePosition = whiteKnight.getPosition();
        var targetPosition = board.getPosition("c6").get();

        var moveAction = actions.stream()
                .filter(Action::isMove)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        assertTrue(moveAction.isPresent());
        assertEquals("Ne5 c6", moveAction.get().getCode());

        moveAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(targetPosition, whiteKnight.getPosition());
        assertTrue(board.isEmpty(sourcePosition));
    }
}