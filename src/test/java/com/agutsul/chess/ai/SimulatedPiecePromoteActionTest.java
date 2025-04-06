package com.agutsul.chess.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.ai.PromoteActionAdapter.SimulatedPiecePromoteAction;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class SimulatedPiecePromoteActionTest {

    @Test
    void testPromoteMoveAction() {
        var pieceType = Piece.Type.QUEEN;

        @SuppressWarnings("unchecked")
        var promoteAction = new SimulatedPiecePromoteAction<>(
                mock(PieceMoveAction.class),
                mock(Observable.class),
                pieceType
        );

        assertEquals(pieceType, promoteAction.getPieceType());
    }

    @Test
    void testPromoteCaptureAction() {
        var pieceType = Piece.Type.QUEEN;

        @SuppressWarnings("unchecked")
        var promoteAction = new SimulatedPiecePromoteAction<>(
                mock(PieceCaptureAction.class),
                mock(Observable.class),
                pieceType
        );

        assertEquals(pieceType, promoteAction.getPieceType());
    }
}
