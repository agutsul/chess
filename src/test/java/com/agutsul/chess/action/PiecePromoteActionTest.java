package com.agutsul.chess.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

@ExtendWith(MockitoExtension.class)
public class PiecePromoteActionTest {

    @Test
    void testWhitePawnPromotionBasedOnMove() {
        var board = new BoardBuilder()
                .withWhitePawn("a7")
                .build();

        board.addObserver(new PieceTypeRequestObserver());

        var pawn = board.getPiece("a7").get();
        var pawnSourcePosition = pawn.getPosition();

        var actions = board.getActions(pawn);

        assertFalse(actions.isEmpty());

        var promotionAction = actions.stream()
                .filter(action -> Action.Type.PROMOTE.equals(action.getType()))
                .findFirst();

        assertTrue(promotionAction.isPresent());
        assertEquals("a7->a8?", promotionAction.get().getCode());

        var targetPosition = board.getPosition("a8").get();
        assertEquals(targetPosition, promotionAction.get().getPosition());

        promotionAction.get().execute();

        assertEquals(targetPosition, pawn.getPosition());
        assertTrue(board.isEmpty(pawnSourcePosition));
    }

    @Test
    void testBlackPawnPromotionBasedOnCapture() {
        var board = new BoardBuilder()
                .withBlackPawn("a2")
                .withWhiteRook("b1")
                .build();

        board.addObserver(new PieceTypeRequestObserver());

        var pawn = board.getPiece("a2").get();
        var pawnSourcePosition = pawn.getPosition();

        var rook = board.getPiece("b1").get();
        assertTrue(rook.isActive());

        var actions = board.getActions(pawn);

        assertFalse(actions.isEmpty());

        var promotionAction = actions.stream()
                .filter(action -> Action.Type.PROMOTE.equals(action.getType()))
                .filter(action -> {
                    var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();
                    return Action.Type.CAPTURE.equals(sourceAction.getType());
                })
                .findFirst();

        assertTrue(promotionAction.isPresent());
        assertEquals("a2xb1?", promotionAction.get().getCode());

        var targetPosition = board.getPosition("b1").get();
        assertEquals(targetPosition, promotionAction.get().getPosition());

        promotionAction.get().execute();

        assertEquals(targetPosition, pawn.getPosition());
        assertTrue(board.isEmpty(pawnSourcePosition));
        assertFalse(rook.isActive());
    }

    // mock interaction with user during piece type selection
    private static class PieceTypeRequestObserver implements Observer {

        @Override
        public void observe(Event event) {
            if (event instanceof RequestPromotionPieceTypeEvent) {
                process((RequestPromotionPieceTypeEvent) event);
            }
        }

        private void process(RequestPromotionPieceTypeEvent event) {
            event.getAction().observe(new PromotionPieceTypeEvent(Piece.Type.QUEEN));
        }
    }
}