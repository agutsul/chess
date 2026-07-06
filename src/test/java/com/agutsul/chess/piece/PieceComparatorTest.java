package com.agutsul.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PieceComparatorTest {

    @Mock
    Piece<?> piece1;
    @Mock
    Piece<?> piece2;

    @Test
    void testEqualPieces() {
        when(piece1.getType())
            .thenReturn(Piece.Type.BISHOP);
        when(piece2.getType())
            .thenReturn(Piece.Type.KNIGHT);

        var comparator = new PieceComparator();
        assertEquals(0, comparator.compare(piece1, piece2));
    }

    @Test
    void testKingAndPawnPieces() {
        when(piece1.getType())
            .thenReturn(Piece.Type.KING);
        when(piece2.getType())
            .thenReturn(Piece.Type.PAWN);

        var comparator = new PieceComparator();
        assertEquals(-1, comparator.compare(piece1, piece2));
    }

    @Test
    void testQueenAndRookPieces() {
        when(piece1.getType())
            .thenReturn(Piece.Type.ROOK);
        when(piece2.getType())
            .thenReturn(Piece.Type.QUEEN);

        var comparator = new PieceComparator();
        assertEquals(1, comparator.compare(piece1, piece2));
    }

    @Test
    void testSorting() {
        when(piece1.getType())
            .thenReturn(Piece.Type.ROOK);
        when(piece2.getType())
            .thenReturn(Piece.Type.PAWN);

        var kingPiece = mock(Piece.class);
        when(kingPiece.getType())
            .thenReturn(Piece.Type.KING);

        var pieces = new ArrayList<Piece<?>>();

        pieces.add(piece1);
        pieces.add(piece2);
        pieces.add(kingPiece);

        pieces.sort(new PieceComparator());

        assertEquals(kingPiece, pieces.getFirst());
        assertEquals(piece2, pieces.getLast());
        assertEquals(piece1, pieces.get(1));
    }
}