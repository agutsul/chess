package com.agutsul.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Color;
import com.agutsul.chess.Colors;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece.Type;

@ExtendWith(MockitoExtension.class)
public class KingPieceImplTest extends AbstractPieceTest {

    private static final Type KING_TYPE = Piece.Type.KING;

    @Test
    void testDefaultKingActionsOnStandardBoard() {
        var expectedPositions = List.of("e1", "e8");

        var board = new StandardBoard();
        var pieces = board.getPieces(KING_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        assertPieceActions(board, Colors.WHITE, KING_TYPE, expectedPositions.get(0));
        assertPieceActions(board, Colors.BLACK, KING_TYPE, expectedPositions.get(1));
    }

    @Test
    void testDefaultKingActionsOnEmptyBoard() {
        var board1 = new BoardBuilder().withWhiteKing("e1").build();
        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("d1", "d2", "e2", "f2", "f1")
        );

        var board2 = new BoardBuilder().withBlackKing("e8").build();
        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("d8", "d7", "e7", "f7", "f8")
        );
    }

    @Test
    void testRandomKingActionsOnEmptyBoard() {
        var board1 = new BoardBuilder().withWhiteKing("e4").build();
        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e4",
                List.of("e5", "e3", "d4", "f4", "d5", "f5", "f3", "d3")
        );

        var board2 = new BoardBuilder().withBlackKing("d5").build();
        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "d5",
                List.of("d6", "d4", "c5", "e5", "c6", "e6", "e4", "c4")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoard() {
        var board1 = new BoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withWhiteKing("e1")
                .build();

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "f2", "f1", "d1"),
                List.of(),
                List.of("0-0", "0-0-0")
        );

        var board2 = new BoardBuilder()
                .withBlackRooks("a8", "h8")
                .withBlackKing("e8")
                .build();

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "f7", "f8", "d8"),
                List.of(),
                List.of("0-0", "0-0-0")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoardCastlilngBlockedByPiece() {
        var board1 = new BoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withWhiteKnight("b1")
                .withWhiteKing("e1")
                .build();

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "f2", "f1", "d1"),
                List.of(),
                List.of("0-0")
        );

        var board2 = new BoardBuilder()
                .withBlackRooks("a8", "h8")
                .withBlackKnight("b8")
                .withBlackKing("e8")
                .build();

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "f7", "f8", "d8"),
                List.of(),
                List.of("0-0")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoardCastlilngBlockedByKingCheck() {
        var board1 = new BoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withBlackBishop("b4")
                .withWhiteKing("e1")
                .build();

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "f2", "f1", "d1")
        );

        var board2 = new BoardBuilder()
                .withBlackRooks("a8", "h8")
                .withWhiteBishop("g6")
                .withBlackKing("e8")
                .build();

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "f7", "f8", "d8")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoardCastlilngBlockedByAttackedPositionOnQueenSide() {
        var board1 = new BoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withBlackQueen("c6")
                .withWhiteKing("e1")
                .build();

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "f2", "f1", "d1"),
                List.of(),
                List.of("0-0")
        );

        var board2 = new BoardBuilder()
                .withBlackRooks("a8", "h8")
                .withWhiteQueen("c7")
                .withBlackKing("e8")
                .build();

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "f7", "f8", "d8"),
                List.of(),
                List.of("0-0")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoardCastlilngBlockedByAttackedPositionOnKingSide() {
        var board1 = new BoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withBlackQueen("f6")
                .withWhiteKing("e1")
                .build();

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "f2", "f1", "d1"),
                List.of(),
                List.of("0-0-0")
        );

        var board2 = new BoardBuilder()
                .withBlackRooks("a8", "h8")
                .withWhiteQueen("f3")
                .withBlackKing("e8")
                .build();

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "f7", "f8", "d8"),
                List.of(),
                List.of("0-0-0")
        );
    }

    @Test
    // https://en.wikipedia.org/wiki/Scholar%27s_mate
    void testKingScholarCheckmate() {
        var board = new StandardBoard();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();
        whitePawn.move(board.getPosition("e4").get());

        var blackPawn = (PawnPiece<Color>) board.getPiece("e7").get();
        blackPawn.move(board.getPosition("e5").get());

        var whiteQueen = (QueenPiece<Color>) board.getPiece("d1").get();
        whiteQueen.move(board.getPosition("h5").get());

        var blackKnight1 = (KnightPiece<Color>) board.getPiece("b8").get();
        blackKnight1.move(board.getPosition("c6").get());

        var whiteBishop = (BishopPiece<Color>) board.getPiece("f1").get();
        whiteBishop.move(board.getPosition("c4").get());

        var blackKnight2 = (KnightPiece<Color>) board.getPiece("g8").get();
        blackKnight2.move(board.getPosition("f6").get());

        whiteQueen.capture(board.getPiece("f7").get());

        var blackKing = (KingPiece<Color>) board.getPiece("e8").get();

        assertTrue(blackKing.isChecked());
        assertTrue(blackKing.isCheckMated());
    }

    @Test
    void testKingCheckMateBlockable() {
        var board = new BoardBuilder()
                .withBlackRook("g8")
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteQueen("c3")
                .build();

        var blackKing = (KingPiece<Color>) board.getPiece("h8").get();

        assertTrue(blackKing.isChecked());
        assertFalse(blackKing.isCheckMated());
    }

    @Test
    void testKingCheckMovable() {
        var board = new BoardBuilder()
                .withBlackRook("e8")
                .withBlackKing("c6")
                .withWhiteBishop("h7")
                .withWhiteKing("e3")
                .build();

        var whiteKing = (KingPiece<Color>) board.getPiece("e3").get();

        assertTrue(whiteKing.isChecked());
        assertFalse(whiteKing.isCheckMated());
    }

    @Test
    void testKingCastlingAction() {
        var board = new BoardBuilder()
                .withWhiteRook("h1")
                .withWhiteKing("e1")
                .build();

        var king = (KingPiece<Color>) board.getPiece("e1").get();
        var position = board.getPosition("g1").get();

        king.castling(position);

        assertEquals(king.getPosition(), position);
    }

    @Test
    void testKingCastlingActionValidation() {
        var board = new BoardBuilder()
                .withWhiteRook("h1")
                .withWhiteKing("e1")
                .build();

        var king = (KingPiece<Color>) board.getPiece("e1").get();
        var position = board.getPosition("f2").get();

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> king.castling(position)
        );

        assertEquals(thrown.getMessage(), "Ke1 invalid castling to f2");
    }

    @Test
    void testKingDisposingThrowsException() {
        var board = new BoardBuilder()
                .withWhiteKing("e1")
                .build();

        var king = board.getPiece("e1").get();
        var thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> ((AbstractPiece<Color>) king).dispose()
        );
        assertEquals(thrown.getMessage(), "Unable to dispose KING piece");
    }
}