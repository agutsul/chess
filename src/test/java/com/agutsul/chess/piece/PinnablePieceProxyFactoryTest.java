package com.agutsul.chess.piece;

import static com.agutsul.chess.piece.PinnablePieceProxyFactory.pinnableProxy;
import static org.apache.commons.lang3.ClassUtils.getAllInterfaces;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.Board;

@ExtendWith(MockitoExtension.class)
public class PinnablePieceProxyFactoryTest {

    @Mock
    private AbstractBoard board;

    @Test
    void testPinnableQueenProxyCreation() {
        testPieceProxyCreation(board, Piece.Type.QUEEN, QueenPiece.class);
    }

    @Test
    void testPinnableKnightProxyCreation() {
        testPieceProxyCreation(board, Piece.Type.KNIGHT, KnightPiece.class);
    }

    @Test
    void testPinnableBishopProxyCreation() {
        testPieceProxyCreation(board, Piece.Type.BISHOP, BishopPiece.class);
    }

    @Test
    void testPinnableRookProxyCreation() {
        testPieceProxyCreation(board, Piece.Type.ROOK, RookPiece.class);
    }

    @Test
    void testPinnablePawnProxyCreation() {
        testPieceProxyCreation(board, Piece.Type.PAWN, PawnPiece.class);
    }

    @Test
    void testPinnableProxyCreationWithNulls() {
        var piece = mock(QueenPiece.class);
        when(piece.getType())
            .thenReturn(Piece.Type.KING);

        assertNull(pinnableProxy(null, piece));
        assertNull(pinnableProxy(board, null));
        assertNull(pinnableProxy(board, piece));
    }

    static <PIECE extends Piece<?> & Pinnable> void testPieceProxyCreation(Board board,
                                                                    Piece.Type pieceType,
                                                                    Class<PIECE> pieceClass) {
        var piece = mock(pieceClass);
        when(piece.getType())
            .thenReturn(pieceType);

        var proxy = pinnableProxy(board, piece);

        assertNotNull(proxy);
        assertTrue(proxy instanceof PieceProxy<?>);

        var allInterfaces = getAllInterfaces(proxy.getClass());
        assertTrue(allInterfaces.contains(pieceClass));
    }
}