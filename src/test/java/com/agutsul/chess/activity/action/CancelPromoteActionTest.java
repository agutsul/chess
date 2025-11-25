package com.agutsul.chess.activity.action;

import static com.agutsul.chess.piece.Piece.isPawn;
import static com.agutsul.chess.piece.Piece.isQueen;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.mock.PieceTypeRequestObserverMock;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.QueenPiece;

@ExtendWith(MockitoExtension.class)
public class CancelPromoteActionTest {

    @Test
    void testCancelPawnPromotionBasedOnMove() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a7")
                .build();

        ((Observable) board).addObserver(new PieceTypeRequestObserverMock());

        var pawn = (PawnPiece<Color>) board.getPiece("a7").get();
        var pawnSourcePosition = pawn.getPosition();

        var actions = board.getActions(pawn);

        assertFalse(actions.isEmpty());

        var promotionAction = actions.stream()
                .filter(Action::isPromote)
                .findFirst();

        assertTrue(promotionAction.isPresent());
        assertEquals("a7 a8?", promotionAction.get().getCode());

        var targetPosition = board.getPosition("a8").get();
        assertEquals(targetPosition, promotionAction.get().getPosition());

        promotionAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(targetPosition, pawn.getPosition());
        assertTrue(board.isEmpty(pawnSourcePosition));

        var promotedPiece = board.getPiece(targetPosition).get();
        assertTrue(isQueen(promotedPiece));

        var cancelAction = new CancelPromoteAction<>(
                new CancelMoveAction<>((QueenPiece<Color>) promotedPiece, pawnSourcePosition)
        );

        cancelAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var piece = board.getPiece("a7").get();
        assertTrue(isPawn(piece));
        assertEquals(pawn, piece);
    }
}