package com.agutsul.chess.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.ai.PromoteActionAdapter.SimulatedPiecePromoteAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PromoteActionAdapterTest {

    @Mock
    Position position;
    @Mock
    PawnPiece<Color> pawnPiece;
    @Mock
    RookPiece<Color> rookPiece;
    @Mock
    Observable observable;

    @Test
    void testPiecePromoteActionAdaptForMoveAction() {
        var moveAction = new PieceMoveAction<>(pawnPiece, position);

        var adapter = new PromoteActionAdapter();

        var actions = adapter.adapt(new PiecePromoteAction<>(moveAction, observable));
        assertEquals(4, actions.size());

        for (var action : actions) {
            assertEquals(moveAction, action.getSource());

            assertTrue(action instanceof SimulatedPiecePromoteAction<?,?>);

            var simulatedAction = (SimulatedPiecePromoteAction<?,?>) action;

            assertNotNull(simulatedAction.getPieceType());
            assertNotEquals(Piece.Type.PAWN, simulatedAction.getPieceType());
            assertNotEquals(Piece.Type.KING, simulatedAction.getPieceType());
        }
    }

    @Test
    void testPiecePromoteActionAdaptForCaptureAction() {
        var captureAction = new PieceCaptureAction<>(pawnPiece, rookPiece);

        var adapter = new PromoteActionAdapter();

        var actions = adapter.adapt(new PiecePromoteAction<>(captureAction, observable));
        assertEquals(4, actions.size());

        for (var action : actions) {
            assertEquals(captureAction, action.getSource());

            assertTrue(action instanceof SimulatedPiecePromoteAction<?,?>);

            var simulatedAction = (SimulatedPiecePromoteAction<?,?>) action;

            assertNotNull(simulatedAction.getPieceType());
            assertNotEquals(Piece.Type.PAWN, simulatedAction.getPieceType());
            assertNotEquals(Piece.Type.KING, simulatedAction.getPieceType());
        }
    }
}