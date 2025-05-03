package com.agutsul.chess.piece.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceBigMoveAction;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.impl.PawnPieceImpl.PawnActionCache;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PawnActionCacheTest {

    @Mock
    PawnPiece<Color> piece;

    @Mock
    PawnPiece<Color> piece2;

    @Mock
    Position position;

    @Mock
    Observable observable;

    @Test
    void testPutAll() {
        var moveAction = new PieceMoveAction<>(piece, position);
        var bigMoveAction = new PieceBigMoveAction<>(piece, position);
        var captureAction = new PieceCaptureAction<>(piece, piece2);
        var enPassantAction = new PieceEnPassantAction<>(piece, piece2, position);
        var promoteAction1 = new PiecePromoteAction<>(moveAction, observable);
        var promoteAction2 = new PiecePromoteAction<>(captureAction, observable);

        Collection<Action<?>> actions = List.of(
                moveAction, bigMoveAction,
                captureAction, enPassantAction,
                promoteAction1, promoteAction2
        );

        var cache = new PawnActionCache();
        cache.putAll(actions);

        assertEquals(2, cache.get(Action.Type.MOVE).size());
        assertEquals(1, cache.get(Action.Type.BIG_MOVE).size());
        assertEquals(2, cache.get(Action.Type.CAPTURE).size());
        assertEquals(1, cache.get(Action.Type.EN_PASSANT).size());
        assertEquals(2, cache.get(Action.Type.PROMOTE).size());
    }
}