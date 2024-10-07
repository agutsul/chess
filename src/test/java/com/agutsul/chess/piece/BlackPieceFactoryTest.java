package com.agutsul.chess.piece;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Colors;
import com.agutsul.chess.board.BoardBuilder;

@ExtendWith(MockitoExtension.class)
public class BlackPieceFactoryTest extends AbstractsPieceFactoryTest {

    public BlackPieceFactoryTest() {
        super(new BlackPieceFactory(new BoardBuilder().build()));
    }

    @Test
    void testCreateKing() {
        assertPiece(pieceFactory.createKing("a1"), Piece.Type.KING, Colors.BLACK);
    }

    @Test
    void testCreateQueen() {
        assertPiece(pieceFactory.createQueen("a1"), Piece.Type.QUEEN, Colors.BLACK);
    }

    @Test
    void testCreateKnight() {
        assertPiece(pieceFactory.createKnight("a1"), Piece.Type.KNIGHT, Colors.BLACK);
    }

    @Test
    void testCreateBishop() {
        assertPiece(pieceFactory.createBishop("a1"), Piece.Type.BISHOP, Colors.BLACK);
    }

    @Test
    void testCreateRook() {
        assertPiece(pieceFactory.createRook("a1"), Piece.Type.ROOK, Colors.BLACK);
    }

    @Test
    void testCreatePawn() {
        assertPiece(pieceFactory.createPawn("a1"), Piece.Type.PAWN, Colors.BLACK);
    }
}
