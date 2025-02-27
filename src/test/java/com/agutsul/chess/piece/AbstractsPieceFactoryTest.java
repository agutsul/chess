package com.agutsul.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.factory.PieceFactory;

abstract class AbstractsPieceFactoryTest {

    protected final PieceFactory pieceFactory;

    AbstractsPieceFactoryTest(PieceFactory pieceFactory) {
        this.pieceFactory = pieceFactory;
    }

    void assertPiece(Piece<Color> piece, Piece.Type type, Color color) {
        assertNotNull(piece);
        assertEquals(piece.getType(),  type);
        assertEquals(piece.getColor(), color);
    }
}
