package com.agutsul.chess.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.ai.PromoteActionAdapter.SimulatedPiecePromoteAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;

@ExtendWith(MockitoExtension.class)
public class SimulatedPiecePromoteActionTest {

    private static final Type QUEEN_PIECE_TYPE = Piece.Type.QUEEN;

    @Mock
    Observable observable;
    @Mock
    PieceMoveAction<Color,PawnPiece<Color>> moveAction;
    @Mock
    PieceCaptureAction<Color,Color,PawnPiece<Color>,?> captureAction;

    @Test
    void testPromoteMoveAction() {
        var promoteAction = new SimulatedPiecePromoteAction<>(
                moveAction,
                observable,
                QUEEN_PIECE_TYPE
        );

        assertEquals(QUEEN_PIECE_TYPE, promoteAction.getPieceType());
    }

    @Test
    void testPromoteCaptureAction() {
        var promoteAction = new SimulatedPiecePromoteAction<>(
                captureAction,
                observable,
                QUEEN_PIECE_TYPE
        );

        assertEquals(QUEEN_PIECE_TYPE, promoteAction.getPieceType());
    }
}