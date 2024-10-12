package com.agutsul.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.PawnPiece.PawnPieceProxy;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
@Disabled
public class PawnProxyTest {

    @Mock
    private Board board;
    @Mock
    private PawnPiece<Color> pawn;
    @Mock
    private PieceFactory pieceFactory;

    private PawnPieceProxy proxy;

    @BeforeEach
    public void setUp() {
        this.proxy = new PawnPieceProxy(board, pawn, 7, pieceFactory);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testPromoteToBishop() {
        var position = mock(Position.class);

        when(board.getActions(eq(pawn)))
            .then(inv -> List.of(new PiecePromoteAction(
                    board,
                    new PieceMoveAction<Color,PawnPiece<Color>>(null, position))
            ));

        doReturn(true).when(pawn).isActive();
        doNothing().when(pawn).promote(any(), any());
        when(pieceFactory.createBishop(eq(position))).thenReturn(mock(BishopPiece.class));

        var origin = (PawnPiece<?>) proxy.origin;

        proxy.promote(position, Piece.Type.BISHOP);

        verify(origin, times(1)).promote(any(), any());
        assertNotEquals(proxy.origin, origin);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testPromoteToKnight() {
        var position = mock(Position.class);

        when(board.getActions(eq(pawn)))
            .then(inv -> List.of(new PiecePromoteAction(
                    board,
                    new PieceMoveAction<Color,PawnPiece<Color>>(null, position))
            ));

        doReturn(true).when(pawn).isActive();
        doNothing().when(pawn).promote(any(), any());
        when(pieceFactory.createKnight(eq(position))).thenReturn(mock(KnightPiece.class));

        var origin = (PawnPiece<?>) proxy.origin;

        proxy.promote(position, Piece.Type.KNIGHT);

        verify(origin, times(1)).promote(any(), any());
        assertNotEquals(proxy.origin, origin);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testPromoteToQueen() {
        var position = mock(Position.class);

        when(board.getActions(eq(pawn)))
            .then(inv -> List.of(new PiecePromoteAction(
                    board,
                    new PieceMoveAction<Color,PawnPiece<Color>>(null, position))
            ));

        doReturn(true).when(pawn).isActive();
        doNothing().when(pawn).promote(any(), any());
        when(pieceFactory.createQueen(eq(position))).thenReturn(mock(QueenPiece.class));

        var origin = (PawnPiece<?>) proxy.origin;

        proxy.promote(position, Piece.Type.QUEEN);

        verify(origin, times(1)).promote(any(), any());
        assertNotEquals(proxy.origin, origin);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testPromoteToRook() {
        var position = mock(Position.class);

        when(board.getActions(eq(pawn)))
            .then(inv -> List.of(new PiecePromoteAction(
                    board,
                    new PieceMoveAction<Color,PawnPiece<Color>>(null, position))));

        doReturn(true).when(pawn).isActive();
        doNothing().when(pawn).promote(any(), any());
        when(pieceFactory.createRook(eq(position))).thenReturn(mock(RookPiece.class));

        var origin = (PawnPiece<?>) proxy.origin;

        proxy.promote(position, Piece.Type.ROOK);

        verify(origin, times(1)).promote(any(), any());
        assertNotEquals(proxy.origin, origin);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testUnsupportedPromotionType() {
        var position = mock(Position.class);

        doReturn(true).when(pawn).isActive();

        when(board.getActions(eq(pawn)))
            .then(inv -> List.of(new PiecePromoteAction(
                    board,
                    new PieceMoveAction<Color,PawnPiece<Color>>(null, position))));

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> proxy.promote(position, Piece.Type.KING)
            );
        assertEquals(thrown.getMessage(), "Unsupported promotion type: " + Piece.Type.KING.name());
    }
}
