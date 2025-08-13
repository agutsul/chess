package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class CancelMoveActionTest {

    @Test
    void testCancelMoveAction() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();
        var actions = board.getActions(whitePawn);

        var sourcePosition = whitePawn.getPosition();
        var targetPosition = board.getPosition("e3").get();

        var moveAction = actions.stream()
                .filter(Action::isMove)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        assertTrue(moveAction.isPresent());

        moveAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var cancelAction = new CancelMoveAction<>(whitePawn, sourcePosition);
        cancelAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(sourcePosition, whitePawn.getPosition());
    }
}