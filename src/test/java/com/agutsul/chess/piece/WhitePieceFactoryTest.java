package com.agutsul.chess.piece;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class WhitePieceFactoryTest extends AbstractsPieceFactoryTest {

    public WhitePieceFactoryTest() {
        super(new WhitePieceFactory(new BoardBuilder().build()));
    }

    @Test
    void testCreateKing() {
        assertPiece(pieceFactory.createKing("a1"), Piece.Type.KING, Colors.WHITE);
    }

    @Test
    void testCreateQueen() {
        assertPiece(pieceFactory.createQueen("a1"), Piece.Type.QUEEN, Colors.WHITE);
    }

    @Test
    void testCreateKnight() {
        assertPiece(pieceFactory.createKnight("a1"), Piece.Type.KNIGHT, Colors.WHITE);
    }

    @Test
    void testCreateBishop() {
        assertPiece(pieceFactory.createBishop("a1"), Piece.Type.BISHOP, Colors.WHITE);
    }

    @Test
    void testCreateRook() {
        assertPiece(pieceFactory.createRook("a1"), Piece.Type.ROOK, Colors.WHITE);
    }

    @Test
    void testCreatePawn() {
        assertPiece(pieceFactory.createPawn("a1"), Piece.Type.PAWN, Colors.WHITE);
    }
}
