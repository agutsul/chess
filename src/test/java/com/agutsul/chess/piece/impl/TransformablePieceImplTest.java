package com.agutsul.chess.piece.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
    Position position;
    @Mock
    AbstractBoard board;
    @Mock
    QueenPiece<Color> queenPiece;
    @Mock
    RookPiece<Color> rookPiece;
    @Mock
    KnightPiece<Color> knightPiece;
    @Mock
    BishopPiece<Color> bishopPiece;
    @Mock
    PawnPiece<Color> pawnPiece;
    @Mock
    PieceFactory<Color> pieceFactory;

    TransformablePieceImpl<?,?> proxy;

    @BeforeEach
    public void setUp() {
        this.proxy = new TransformablePieceImpl<>(board, pieceFactory, pawnPiece, 7);
    }

    @Test
    void testGetPosiotionsNonPromotedPawn() {
        assertEquals(pawnPiece.getPositions(), proxy.getPositions());
    }

    @Test
    void testGetPosiotionsForPromotedPawn() {
        when(pawnPiece.getPositions())
            .thenReturn(List.of(position));

        doNothing()
            .when(pawnPiece).dispose(any());

        when(board.getActions(any(), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createBishop(eq(position)))
            .thenReturn(bishopPiece);

        proxy.promote(position, Piece.Type.BISHOP);

        var promotedPositions = proxy.getPositions();
        var pawnPositions = pawnPiece.getPositions();

        assertTrue(promotedPositions.containsAll(pawnPositions));
    }

    @Test
    void testPromoteToBishop() {
        doNothing()
            .when(pawnPiece).dispose(any());

        when(board.getActions(any(), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createBishop(eq(position)))
            .thenReturn(bishopPiece);

        var origin = (PawnPiece<?>) proxy.origin;

        proxy.promote(position, Piece.Type.BISHOP);

        verify(origin, times(1)).dispose(any());
        verify(origin, never()).promote(any(), any());

        assertNotEquals(proxy.origin, origin);
    }

    @Test
    void testPromoteToKnight() {
        doNothing()
            .when(pawnPiece).dispose(any());

        when(board.getActions(any(), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createKnight(eq(position)))
            .thenReturn(knightPiece);

        var origin = (PawnPiece<?>) proxy.origin;

        proxy.promote(position, Piece.Type.KNIGHT);

        verify(origin, times(1)).dispose(any());
        verify(origin, never()).promote(any(), any());

        assertNotEquals(proxy.origin, origin);
    }

    @Test
    void testPromoteToQueen() {
        doNothing()
            .when(pawnPiece).dispose(any());

        when(board.getActions(any(), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createQueen(eq(position)))
            .thenReturn(queenPiece);

        var origin = (PawnPiece<?>) proxy.origin;

        proxy.promote(position, Piece.Type.QUEEN);

        verify(origin, times(1)).dispose(any());
        verify(origin, never()).promote(any(), any());

        assertNotEquals(proxy.origin, origin);
    }

    @Test
    void testPromoteToRook() {
        doNothing()
            .when(pawnPiece).dispose(any());

        when(board.getActions(any(), eq(Action.Type.PROMOTE)))
            .then(inv -> {
                var piece = inv.getArgument(0, PawnPiece.class);
                var moveAction = new PieceMoveAction<>(piece, position);

                return List.of(new PiecePromoteAction<>(moveAction, board));
            });

        when(pieceFactory.createRook(eq(position)))
            .thenReturn(rookPiece);

        var origin = (PawnPiece<?>) proxy.origin;

        proxy.promote(position, Piece.Type.ROOK);

        verify(origin, times(1)).dispose(any());
        verify(origin, never()).promote(any(), any());

        assertNotEquals(proxy.origin, origin);
    }

    @Test
    void testUnsupportedPromotionType() {
        when(board.getActions(any(), eq(Action.Type.PROMOTE)))
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
        when(pawnPiece.isMoved()).thenReturn(moved);

        assertEquals(proxy.isMoved(), moved);
        verify(pawnPiece, times(1)).isMoved();
    }
}