package com.agutsul.chess.piece.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class PieceCacheImplTest {

    @Test
    void testGetAllPieces() {
        var pieces = createPieces();
        var cache = new PieceCacheImpl(pieces);

        assertEquals(pieces.size(), cache.getAll().size());
    }

    @Test
    void testGetByColor() {
        var cache = new PieceCacheImpl(createPieces());

        assertEquals(16, cache.get(Colors.WHITE).size());
        assertEquals(16, cache.get(Colors.BLACK).size());
    }

    @Test
    void testGetByPieceType() {
        var cache = new PieceCacheImpl(createPieces());

        assertEquals(4, cache.get(Piece.Type.ROOK).size());
        assertEquals(2, cache.get(Piece.Type.QUEEN).size());
        assertEquals(16, cache.get(Piece.Type.PAWN).size());
    }

    @Test
    void testGetByColorAndPieceType() {
        var cache = new PieceCacheImpl(createPieces());
        for (var color : Colors.values()) {
            assertEquals(1, cache.get(color, Piece.Type.QUEEN).size());
            assertEquals(1, cache.get(color, Piece.Type.KING).size());
            assertEquals(2, cache.get(color, Piece.Type.ROOK).size());
            assertEquals(2, cache.get(color, Piece.Type.BISHOP).size());
            assertEquals(2, cache.get(color, Piece.Type.KNIGHT).size());
            assertEquals(8, cache.get(color, Piece.Type.PAWN).size());
        }
    }

    @Test
    void testGetByPosition() {
        var cache = new PieceCacheImpl(createPieces());

        var emptyPosition = PositionFactory.positionOf("e3");
        assertTrue(cache.get(emptyPosition).isEmpty());

        var nonEmptyPosition = PositionFactory.positionOf("e2");
        assertTrue(cache.get(nonEmptyPosition).isPresent());
    }

    @SuppressWarnings("unchecked")
    private static Collection<Piece<?>> createPieces() {
        var board = new StandardBoard();
        Collection<?> pieces = board.getPieces();

        return (Collection<Piece<?>>) pieces;
    }
}