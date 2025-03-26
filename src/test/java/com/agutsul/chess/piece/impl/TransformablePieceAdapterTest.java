package com.agutsul.chess.piece.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class TransformablePieceAdapterTest {

    @Test
    void testIsPinned() {
        var pawn = mock(PawnPiece.class);

        var adapter = new TransformablePieceAdapter<>(pawn);
        adapter.isPinned();

        verify(pawn, times(1)).isPinned();
    }

    @Test
    void testIsBlocked() {
        var pawn = mock(PawnPiece.class);

        var adapter = new TransformablePieceAdapter<>(pawn);
        adapter.isBlocked();

        verify(pawn, times(1)).isBlocked();
    }

    @Test
    void testEnPassant() {
        var pawn = mock(PawnPiece.class);

        var adapter = new TransformablePieceAdapter<>(pawn);
        adapter.enpassant(mock(PawnPiece.class), mock(Position.class));

        verify(pawn, times(1)).enpassant(any(), any());
    }

    @Test
    void testUnEnPassant() {
        var pawn = mock(PawnPiece.class);

        var adapter = new TransformablePieceAdapter<>(pawn);
        adapter.unenpassant(mock(PawnPiece.class));

        verify(pawn, times(1)).unenpassant(any());
    }

    @Test
    void testCastling() {
        var proxy = mock(PromotablePieceProxy.class);

        var adapter = new TransformablePieceAdapter<>(proxy);
        adapter.castling(mock(Position.class));

        verify(proxy, times(1)).castling(any());
    }

    @Test
    void testUnCastling() {
        var proxy = mock(PromotablePieceProxy.class);

        var adapter = new TransformablePieceAdapter<>(proxy);
        adapter.uncastling(mock(Position.class));

        verify(proxy, times(1)).uncastling(any());
    }

    @Test
    void testPromote() {
        var proxy = mock(PromotablePieceProxy.class);

        var adapter = new TransformablePieceAdapter<>(proxy);
        adapter.promote(mock(Position.class), Piece.Type.QUEEN);

        verify(proxy, times(1)).promote(any(), any());
    }

    @Test
    void testDemote() {
        var proxy = mock(PromotablePieceProxy.class);

        var adapter = new TransformablePieceAdapter<>(proxy);
        adapter.demote();

        verify(proxy, times(1)).demote();
    }
}