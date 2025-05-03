package com.agutsul.chess.piece.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.factory.PieceFactory;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class TransformablePieceImplTest {

    @Mock
    private AbstractBoard board;
    @Mock
    private PawnPiece<Color> pawn;
    @Mock
    private PieceFactory<Color> pieceFactory;

    private TransformablePieceImpl<?,?> proxy;

    @BeforeEach
    public void setUp() {
        this.proxy = new TransformablePieceImpl<>(board, pieceFactory, pawn, 7);
    }

    @Test
    void testGetPosiotionsNonPromotedPawn() {
        assertEquals(pawn.getPositions(), proxy.getPositions());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetPosiotionsForPromotedPawn() {
        var position = mock(Position.class);

        when(pawn.getPositions())
            .thenReturn(List.of(position));

        doNothing()
            .when(pawn).dispose(any());

        when(board.getActions(eq(pawn), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createBishop(eq(position)))
            .thenReturn(mock(BishopPiece.class));

        proxy.promote(position, Piece.Type.BISHOP);

        var promotedPositions = proxy.getPositions();
        var pawnPositions = pawn.getPositions();

        assertTrue(promotedPositions.containsAll(pawnPositions));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPromoteToBishop() {
        var position = mock(Position.class);

        doNothing()
            .when(pawn).dispose(any());

        when(board.getActions(eq(pawn), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createBishop(eq(position)))
            .thenReturn(mock(BishopPiece.class));

        var origin = (PawnPiece<Color>) proxy.origin;

        proxy.promote(position, Piece.Type.BISHOP);

        verify(origin, times(1)).dispose(any());
        verify(origin, never()).promote(any(), any());

        assertNotEquals(proxy.origin, origin);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPromoteToKnight() {
        var position = mock(Position.class);

        doNothing()
            .when(pawn).dispose(any());

        when(board.getActions(eq(pawn), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createKnight(eq(position)))
            .thenReturn(mock(KnightPiece.class));

        var origin = (PawnPiece<Color>) proxy.origin;

        proxy.promote(position, Piece.Type.KNIGHT);

        verify(origin, times(1)).dispose(any());
        verify(origin, never()).promote(any(), any());

        assertNotEquals(proxy.origin, origin);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPromoteToQueen() {
        var position = mock(Position.class);

        doNothing()
            .when(pawn).dispose(any());

        when(board.getActions(eq(pawn), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createQueen(eq(position)))
            .thenReturn(mock(QueenPiece.class));

        var origin = (PawnPiece<Color>) proxy.origin;

        proxy.promote(position, Piece.Type.QUEEN);

        verify(origin, times(1)).dispose(any());
        verify(origin, never()).promote(any(), any());

        assertNotEquals(proxy.origin, origin);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPromoteToRook() {
        var position = mock(Position.class);

        doNothing()
            .when(pawn).dispose(any());

        when(board.getActions(eq(pawn), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createRook(eq(position)))
            .thenReturn(mock(RookPiece.class));

        var origin = (PawnPiece<Color>) proxy.origin;

        proxy.promote(position, Piece.Type.ROOK);

        verify(origin, times(1)).dispose(any());
        verify(origin, never()).promote(any(), any());

        assertNotEquals(proxy.origin, origin);
    }

    @Test
    void testUnsupportedPromotionType() {
        var position = mock(Position.class);

        when(board.getActions(eq(pawn), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
        });

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> proxy.promote(position, Piece.Type.KING)
            );

        var expectedMessage = String.format(
                "Unsupported promotion type: %s",
                Piece.Type.KING.name()
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    void testIsMoved() {
        var moved = false;
        when(pawn.isMoved()).thenReturn(moved);

        assertEquals(proxy.isMoved(), moved);
        verify(pawn, times(1)).isMoved();
    }
}