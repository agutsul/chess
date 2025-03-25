package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;

@ExtendWith(MockitoExtension.class)
public class CancelMoveActionTest {

    @Test
    void testCancelMoveAction() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .build();

        var whitePawn = board.getPiece("e2").get();
        var actions = board.getActions(whitePawn);

        var sourcePosition = whitePawn.getPosition();
        var targetPosition = board.getPosition("e3").get();

        var moveAction = actions.stream()
                .filter(Action::isMove)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        moveAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        @SuppressWarnings({ "rawtypes", "unchecked" })
        var cancelAction = new CancelMoveAction(whitePawn, sourcePosition);
        cancelAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(sourcePosition, whitePawn.getPosition());
    }
}