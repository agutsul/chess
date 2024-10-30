package com.agutsul.chess.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Color;
import com.agutsul.chess.Colors;
import com.agutsul.chess.piece.Disposable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class BoardImplTest {

    private static final PositionFactory POSITION_FACTORY = PositionFactory.INSTANCE;

    private final Board board;

    BoardImplTest() {
        this.board = new StandardBoard();
    }

    @Test
    void testGetPiecesByColorAndNoPositions() {
        assertEquals(board.getPieces(Colors.WHITE, "a1", "h2").size(), 2);
    }

    @Test
    void testGetPieces() {
        assertNotNull(board);
        assertFalse(board.getPieces().isEmpty());
        assertEquals(board.getPieces().size(), 32);
    }

    @Test
    void testGetPieceByPositionCode() {
        assertTrue(board.getPiece("a1").isPresent());
    }

    @Test
    void testGetPositionByValidCode() {
        assertTrue(board.getPosition("a1").isPresent());
    }

    @Test
    void testGetPositionByInvalidCode() {
        assertTrue(board.getPosition("x1").isEmpty());
    }

    @Test
    void testGetPositionByValidCoordinates() {
        assertTrue(board.getPosition(Position.MIN, Position.MIN).isPresent());
    }

    @Test
    void testGetPositionByInvalidCoordinates() {
        assertTrue(board.getPosition(Position.MAX, Position.MAX).isEmpty());
    }

    @Test
    void testInitialBoardSetup() {
        assertEquals(board.getPieces(Colors.WHITE).size(), 16);
        assertEquals(board.getPieces(Colors.BLACK).size(), 16);
    }

    @Test
    void testInitialEmptyBoardSetup() {
        var emptyCodes = new ArrayList<String>();
        for (var str : Position.LABELS) {
            for (int i = 3; i < 7; i++) {
                emptyCodes.add(String.format("%s%d", str, i));
            }
        }

        for (var code : emptyCodes) {
            assertTrue(board.isEmpty(POSITION_FACTORY.createPosition(code)));
        }
    }

    @Test
    void testInitialPawnBoardSetup() {
        testInitialBoardPieceSetup(Colors.WHITE, Piece.Type.PAWN,
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"
            );
        testInitialBoardPieceSetup(Colors.BLACK, Piece.Type.PAWN,
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"
            );
    }

    @Test
    void testInitialKingBoardSetup() {
        testInitialBoardPieceSetup(Colors.WHITE, Piece.Type.KING, "e1");
        testInitialBoardPieceSetup(Colors.BLACK, Piece.Type.KING, "e8");
    }

    @Test
    void testInitialQueenBoardSetup() {
        testInitialBoardPieceSetup(Colors.WHITE, Piece.Type.QUEEN, "d1");
        testInitialBoardPieceSetup(Colors.BLACK, Piece.Type.QUEEN, "d8");
    }

    @Test
    void testInitialBishopBoardSetup() {
        testInitialBoardPieceSetup(Colors.WHITE, Piece.Type.BISHOP, "f1", "c1");
        testInitialBoardPieceSetup(Colors.BLACK, Piece.Type.BISHOP, "f8", "c8");
    }

    @Test
    void testInitialKnightBoardSetup() {
        testInitialBoardPieceSetup(Colors.WHITE, Piece.Type.KNIGHT, "g1", "b1");
        testInitialBoardPieceSetup(Colors.BLACK, Piece.Type.KNIGHT, "g8", "b8");
    }

    @Test
    void testInitialRookBoardSetup() {
        testInitialBoardPieceSetup(Colors.WHITE, Piece.Type.ROOK, "h1", "a1");
        testInitialBoardPieceSetup(Colors.BLACK, Piece.Type.ROOK, "h8", "a8");
    }

    @Test
    void testIsChecked() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withWhiteQueen("a1")
                .build();

        assertTrue(board.isChecked(Colors.BLACK));
    }

    @Test
    void testIsCheckMated() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withBlackBishop("g8")
                .withWhiteBishop("e5")
                .withWhiteKing("g6")
                .build();

        assertTrue(board.isCheckMated(Colors.BLACK));
    }

    @Test
    void testToString() throws IOException, URISyntaxException {
        var fileName = "standard_board.txt";
        var file = new File(getClass().getClassLoader().getResource(fileName).toURI());
        var standardBoard = Files.readString(file.toPath());
        assertEquals(standardBoard, board.toString());
    }

    @Test
    void testGetCapturedPiece() {
        var board = new BoardBuilder()
                .withWhitePawn("a2")
                .build();

        var pawn = board.getPiece("a2").get();
        ((Disposable) pawn).dispose();

        var pawn2 = board.getCapturedPiece("a2").get();
        assertEquals(pawn, pawn2);
    }

    @Test
    void testCalculateValue() {
        var board = new StandardBoard();

        assertEquals(439,  board.calculateValue(Colors.WHITE));
        assertEquals(-439, board.calculateValue(Colors.BLACK));
    }

    private void testInitialBoardPieceSetup(Color color, Piece.Type pieceType, String... position) {
        var pieces = board.getPieces(color, pieceType);
        assertFalse(pieces.isEmpty());
        assertEquals(pieces.size(), Arrays.asList(position).size());

        var piecePositions = pieces.stream().map(Piece::getPosition).toList();
        var expectedPositions = Stream.of(position)
                .map(code -> POSITION_FACTORY.createPosition(code))
                .toList();

        assertTrue(piecePositions.containsAll(expectedPositions));
    }
}
