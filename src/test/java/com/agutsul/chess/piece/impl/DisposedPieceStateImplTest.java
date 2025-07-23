package com.agutsul.chess.piece.impl;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class DisposedPieceStateImplTest {

    @Mock
    PawnPiece<?> piece;

    @Test
    void testGetDisposedAt() {
        var instant = now();
        var state = new DisposedPieceStateImpl<>(instant);
        assertEquals(Optional.of(instant), state.getDisposedAt());
    }

    @Test
    void testCalculateActions() {
        var state = new DisposedPieceStateImpl<>(now());
        assertTrue(state.calculateActions(piece).isEmpty());
    }

    @Test
    void testCalculateActionsWithType() {
        var state = new DisposedPieceStateImpl<>(now());
        assertTrue(state.calculateActions(piece, Action.Type.MOVE).isEmpty());
    }

    @Test
    void testCalculateImpacts() {
        var state = new DisposedPieceStateImpl<>(now());
        assertTrue(state.calculateImpacts(piece).isEmpty());
    }

    @Test
    void testCalculateImpactsWithType() {
        var state = new DisposedPieceStateImpl<>(now());
        assertTrue(state.calculateImpacts(piece, Impact.Type.PIN).isEmpty());
    }

    @Test
    void testMove() {
        var state = spy(new DisposedPieceStateImpl<>(now()));
        state.move(piece, mock(Position.class));
        verify(state, times(1)).move(any(), any());
    }

    @Test
    void testCapture() {
        var state = spy(new DisposedPieceStateImpl<>(now()));
        state.capture(piece, mock(Piece.class));
        verify(state, times(1)).capture(any(), any());
    }
}