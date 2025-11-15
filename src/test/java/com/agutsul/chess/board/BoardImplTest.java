package com.agutsul.chess.board;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class BoardImplTest {

    private final Board board;

    BoardImplTest() {
        this.board = new StandardBoard();
    }

    @Test
    void testGetPiecesByColorAndNoPositions() {
        assertEquals(2, board.getPieces(Colors.WHITE, "a1", "h2").size());
    }

    @Test
    void testGetPieces() {
        assertNotNull(board);
        assertFalse(board.getPieces().isEmpty());
        assertEquals(32, board.getPieces().size());
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
        assertEquals(16, board.getPieces(Colors.WHITE).size());
        assertEquals(16, board.getPieces(Colors.BLACK).size());
    }

    @Test
    void testGetLineByValidPositions() {
        // vertical line
        assertTrue(board.getLine(positionOf("a1"), positionOf("a3")).isPresent());
        // diagonal lines
        assertTrue(board.getLine(positionOf("a1"), positionOf("d4")).isPresent());
        assertTrue(board.getLine(positionOf("h8"), positionOf("b2")).isPresent());
        // horizontal line
        assertTrue(board.getLine(positionOf("a8"), positionOf("h8")).isPresent());
    }

    @Test
    void testGetLineBySamePositions() {
        var position = positionOf("a8");
        assertTrue(board.getLine(position, position).isEmpty());
    }

    @Test
    void testGetLineByInvalidPositions() {
        assertTrue(board.getLine(positionOf("a1"), positionOf("b3")).isEmpty());
        assertTrue(board.getLine(positionOf("a1"), positionOf("c2")).isEmpty());
        assertTrue(board.getLine(positionOf("a1"), positionOf("g4")).isEmpty());
    }

    @Test
    void testGetLineByBothNullPositions() {
        var position = (Position) null;
        assertTrue(board.getLine(position, position).isEmpty());
    }

    @Test
    void testGetLineByNullPositions() {
        assertTrue(board.getLine((Position) null, positionOf("a1")).isEmpty());
    }

    @Test
    void testGetLines() {
        assertEquals(4, board.getLines(positionOf("d4")).size());
        assertEquals(3, board.getLines(positionOf("a1")).size());
    }

    @Test
    void testGetLinesByNullPosition() {
        assertTrue(board.getLines((Position) null).isEmpty());
    }

    @Test
    void testGetPiecesByLine() {
        var pieces = board.getPieces(board.getLine("a1","a8").get());

        assertFalse(pieces.isEmpty());
        assertEquals(4, pieces.size());

        var whiteRook = board.getPiece("a1").get();
        assertTrue(pieces.contains(whiteRook));

        var whitePawn = board.getPiece("a2").get();
        assertTrue(pieces.contains(whitePawn));

        var blackRook = board.getPiece("a8").get();
        assertTrue(pieces.contains(blackRook));

        var blackPawn = board.getPiece("a7").get();
        assertTrue(pieces.contains(blackPawn));
    }

    @Test
    void testGetPieceByInvalidPositions() {
        assertTrue(board.getPieces(emptyList()).isEmpty());
        assertTrue(board.getPieces((List<Position>) null).isEmpty());
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
            assertTrue(board.isEmpty(positionOf(code)));
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
    void testToString() throws IOException, URISyntaxException {
        var resource = getClass().getClassLoader().getResource("standard_board.txt");
        var file = new File(resource.toURI());
        var standardBoard = Files.readString(file.toPath());
        assertEquals(standardBoard, board.toString());
    }

    @Test
    void testGetCapturedPiece() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .withBlackPawn("b3")
                .build();

        var blackPawn = board.getPiece("b3").get();
        var whitePawn = board.getPiece("a2").get();

        var captureAction = board.getActions(blackPawn, Action.Type.CAPTURE).stream()
                .findFirst()
                .get();

        captureAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var pawn2 = board.getCapturedPiece("a2", whitePawn.getColor()).get();
        assertEquals(whitePawn, pawn2);
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
        assertEquals(pieces.size(), asList(position).size());

        var piecePositions = pieces.stream().map(Piece::getPosition).toList();
        var expectedPositions = Stream.of(position)
                .map(PositionFactory::positionOf)
                .toList();

        assertTrue(piecePositions.containsAll(expectedPositions));
    }
}