package com.agutsul.chess.piece.cache;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class PieceCacheImplTest {

    @AutoClose
    ExecutorService executorService = newSingleThreadExecutor();

    @Test
    void testGetActiveAllPieces() {
        var pieces = createPieces();

        var cache = new PieceCacheImpl(pieces, executorService);
        cache.refresh();

        assertEquals(pieces.size(), cache.getActive().size());
    }

    @Test
    void testGetActiveByColor() {
        var cache = new PieceCacheImpl(createPieces(), executorService);
        cache.refresh();

        assertEquals(16, cache.getActive(Colors.WHITE).size());
        assertEquals(16, cache.getActive(Colors.BLACK).size());
    }

    @Test
    void testGetActiveByPieceType() {
        var cache = new PieceCacheImpl(createPieces(), executorService);
        cache.refresh();

        assertEquals(4, cache.getActive(Piece.Type.ROOK).size());
        assertEquals(2, cache.getActive(Piece.Type.QUEEN).size());
        assertEquals(16, cache.getActive(Piece.Type.PAWN).size());
    }

    @Test
    void testGetActiveByColorAndPieceType() {
        var cache = new PieceCacheImpl(createPieces(), executorService);
        cache.refresh();

        for (var color : Colors.values()) {
            assertEquals(1, cache.getActive(color, Piece.Type.QUEEN).size());
            assertEquals(1, cache.getActive(color, Piece.Type.KING).size());
            assertEquals(2, cache.getActive(color, Piece.Type.ROOK).size());
            assertEquals(2, cache.getActive(color, Piece.Type.BISHOP).size());
            assertEquals(2, cache.getActive(color, Piece.Type.KNIGHT).size());
            assertEquals(8, cache.getActive(color, Piece.Type.PAWN).size());
        }
    }

    @Test
    void testGetActiveByPosition() {
        var cache = new PieceCacheImpl(createPieces(), executorService);
        cache.refresh();

        var emptyPosition = PositionFactory.positionOf("e3");
        assertTrue(cache.getActive(emptyPosition).isEmpty());

        var nonEmptyPosition = PositionFactory.positionOf("e2");
        assertTrue(cache.getActive(nonEmptyPosition).isPresent());
    }

    private static Collection<Piece<?>> createPieces() {
        var board = new StandardBoard();
        return board.getPieces().stream()
                .collect(toList());
        }
}