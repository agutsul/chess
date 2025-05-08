package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class ActionFilterTest {

    @Mock
    PawnPiece<Color> piece;

    @Mock
    PawnPiece<Color> piece2;

    @Mock
    Position position;

    @Mock
    Observable observable;

    @Test
    void testActionFilter() {
        var moveAction = new PieceMoveAction<>(piece, position);
        var bigMoveAction = new PieceBigMoveAction<>(piece, position);
        var captureAction = new PieceCaptureAction<>(piece, piece2);
        var promoteAction1 =  new PiecePromoteAction<>(moveAction, observable);
        var promoteAction2 =  new PiecePromoteAction<>(captureAction, observable);
        var enPassantAction = new PieceEnPassantAction<>(piece, piece2, position);

        Collection<Action<?>> actions = List.of(
                moveAction, bigMoveAction,
                captureAction, enPassantAction,
                promoteAction1, promoteAction2
        );

        assertActionFilter(actions, PieceMoveAction.class,      2);
        assertActionFilter(actions, PieceBigMoveAction.class,   1);
        assertActionFilter(actions, PieceCaptureAction.class,   2);
        assertActionFilter(actions, PieceEnPassantAction.class, 1);
        assertActionFilter(actions, PiecePromoteAction.class,   2);
    }

    static <A extends Action<?>> void assertActionFilter(Collection<Action<?>> actions,
                                                         Class<A> actionClass, int expectedCounter) {

        var filter = new ActionFilter<>(actionClass);
        var filtered = filter.apply(actions);

        assertEquals(filtered.size(), expectedCounter);

        filtered.forEach(action -> assertEquals(action.getClass(), actionClass));
    }
}