package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.piece.Piece.isKnight;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteForkImpact;
import com.agutsul.chess.activity.impact.PieceAbsolutePinImpact;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativePinImpact;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;

@ExtendWith(MockitoExtension.class)
public class KnightPieceImplTest extends AbstractPieceTest {

    private static final Type KNIGHT_TYPE = Piece.Type.KNIGHT;

    @Test
    void testDefaultKnightActionsOnStandardBoard() {
        var expectedPositions = List.of("b1", "g1", "b8", "g8");

        var board = new StandardBoard();
        var pieces = board.getPieces(KNIGHT_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        assertPieceActions(board, Colors.WHITE, KNIGHT_TYPE, expectedPositions.get(0), List.of("a3", "c3"));
        assertPieceActions(board, Colors.WHITE, KNIGHT_TYPE, expectedPositions.get(1), List.of("f3", "h3"));

        assertPieceActions(board, Colors.BLACK, KNIGHT_TYPE, expectedPositions.get(2), List.of("a6", "c6"));
        assertPieceActions(board, Colors.BLACK, KNIGHT_TYPE, expectedPositions.get(3), List.of("f6", "h6"));
    }

    @Test
    void testDefaultKnightActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteKnight("b1").build();
        assertPieceActions(board1, Colors.WHITE, KNIGHT_TYPE, "b1", List.of("a3", "c3", "d2"));

        var board2 = new LabeledBoardBuilder().withWhiteKnight("g1").build();
        assertPieceActions(board2, Colors.WHITE, KNIGHT_TYPE, "g1", List.of("f3", "h3", "e2"));

        var board3 = new LabeledBoardBuilder().withBlackKnight("b8").build();
        assertPieceActions(board3, Colors.BLACK, KNIGHT_TYPE, "b8", List.of("a6", "c6", "d7"));

        var board4 = new LabeledBoardBuilder().withBlackKnight("g8").build();
        assertPieceActions(board4, Colors.BLACK, KNIGHT_TYPE, "g8", List.of("f6", "h6", "e7"));
    }

    @Test
    void testRandomKnightActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteKnight("d5").build();
        assertPieceActions(board1, Colors.WHITE, KNIGHT_TYPE, "d5",
                List.of("b6", "c7", "e7", "f6", "e3", "f4", "c3", "b4"));

        var board2 = new LabeledBoardBuilder().withBlackKnight("e4").build();
        assertPieceActions(board2, Colors.BLACK, KNIGHT_TYPE, "e4",
                List.of("d2", "c3", "c5", "d6", "f6", "g5", "g3", "f2"));
    }

    @Test
    void testKnightCaptureActionOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withBlackPawn("a3")
                .withWhiteKnight("b1")
                .build();

        assertPieceActions(board1, Colors.WHITE, KNIGHT_TYPE, "b1", List.of("c3", "d2"), List.of("a3"));

        var board2 = new LabeledBoardBuilder()
                .withWhitePawn("h6")
                .withBlackKnight("g8")
                .build();

        assertPieceActions(board2, Colors.BLACK, KNIGHT_TYPE, "g8", List.of("f6", "e7"), List.of("h6"));
    }

    @Test
    void testKnightRelativePinImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackRook("b8")
                .withBlackKing("c8")
                .withBlackKnight("e6")
                .withWhiteKnight("b4")
                .withWhiteQueen("b1")
                .withWhiteBishop("f5")
                .withWhiteKing("d1")
                .build();

        var whiteKnight = board.getPiece("b4").get();
        var pinImpacts  = board.getImpacts(whiteKnight, Impact.Type.PIN);
        assertFalse(pinImpacts.isEmpty());

        var relativePinImpacts = pinImpacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .filter(PiecePinImpact::isRelative)
                .map(impact -> (PieceRelativePinImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(relativePinImpacts.isEmpty());

        var whiteQueen = board.getPiece("b1").get();
        var blackRook  = board.getPiece("b8").get();

        var relativePinImpact = relativePinImpacts.getFirst();

        assertEquals(whiteQueen, relativePinImpact.getDefended());
        assertEquals(blackRook,  relativePinImpact.getAttacker());
    }

    @Test
    void testKnightAbsolutePinImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackRook("b8")
                .withBlackKing("c8")
                .withBlackKnight("e6")
                .withWhiteKnight("b4")
                .withWhiteQueen("b1")
                .withWhiteBishop("f5")
                .withWhiteKing("d1")
                .build();

        var blackKnight = board.getPiece("e6").get();
        var pinImpacts  = board.getImpacts(blackKnight, Impact.Type.PIN);
        assertFalse(pinImpacts.isEmpty());

        var absolutePinImpacts = pinImpacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .filter(PiecePinImpact::isAbsolute)
                .map(impact -> (PieceAbsolutePinImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(absolutePinImpacts.isEmpty());

        var whiteBishop = board.getPiece("f5").get();
        var blackKing   = board.getPiece("c8").get();

        var relativePinImpact = absolutePinImpacts.getFirst();

        assertEquals(blackKing,   relativePinImpact.getDefended());
        assertEquals(whiteBishop, relativePinImpact.getAttacker());
    }

    @Test
    void testKnightAbsoluteForkImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("c1")
                .withWhiteKnight("b6")
                .withWhiteRooks("f3","h3")
                .withBlackKing("d7")
                .withBlackRook("a8")
                .withBlackPawn("g4")
                .build();

        var whiteKnight = board.getPiece("b6").get();

        var forkImpacts = board.getImpacts(whiteKnight, Impact.Type.FORK);
        assertFalse(forkImpacts.isEmpty());

        var absoluteForkImpacts = forkImpacts.stream()
                .map(impact -> (PieceForkImpact<?,?,?,?>) impact)
                .filter(PieceForkImpact::isAbsolute)
                .map(impact -> (PieceAbsoluteForkImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertEquals(1, absoluteForkImpacts.size());

        var absoluteForkImpact = absoluteForkImpacts.getFirst();

        var forkedImpacts = new ArrayList<>(absoluteForkImpact.getTarget());
        assertEquals(whiteKnight, absoluteForkImpact.getSource());
        assertEquals(2, forkedImpacts.size());

        assertEquals(Piece.Type.KING, forkedImpacts.getFirst().getTarget().getType());
        assertEquals(Piece.Type.ROOK, forkedImpacts.getLast().getTarget().getType());

        var impactTypes = List.of(Impact.Type.ATTACK, Impact.Type.CHECK);

        forkedImpacts.forEach(impact -> {
            assertTrue(impactTypes.contains(impact.getType()));
            assertEquals(whiteKnight.getPosition(), impact.getPosition());
            assertTrue(isKnight(impact.getSource()));
            assertTrue(impact.getLine().isEmpty());
        });
    }

    @Test
    void testKnightBlockImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("g1")
                .withWhiteRooks("e1","c7")
                .withWhiteBishop("a5")
                .withWhitePawns("f2","g2","h3")
                .withBlackKing("g8")
                .withBlackQueen("e5")
                .withBlackRook("e8")
                .withBlackKnight("c5")
                .withBlackPawns("f7","g7","h7")
                .build();

        var blackKnight = board.getPiece("c5").get();
        var blockImpacts = new ArrayList<>(
                board.getImpacts(blackKnight, Impact.Type.BLOCK)
        );

        assertFalse(blockImpacts.isEmpty());
        assertEquals(2, blockImpacts.size());

        var blockImpact = (PieceBlockImpact<?,?,?,?,?>) blockImpacts.getFirst();
        assertEquals(blackKnight, blockImpact.getBlocker());

        var whiteRook = board.getPiece("e1").get();
        assertEquals(whiteRook, blockImpact.getAttacker());

        var blackQueen = board.getPiece("e5").get();
        assertEquals(blackQueen, blockImpact.getAttacked());

        var blockedLine = blockImpact.getLine();
        assertFalse(blockedLine.isEmpty());

        var position = board.getPosition("e4").get();
        assertEquals(position, blockImpact.getPosition());
        assertTrue(blockedLine.contains(position));
    }

    @Test
    void testKnightInterferenceImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("g1")
                .withWhiteQueen("e2")
                .withWhiteRook("e1")
                .withWhiteKnight("f5")
                .withWhitePawns("a2","f2","g2","e5","h3")
                .withBlackKing("c8")
                .withBlackQueen("d2")
                .withBlackRook("d8")
                .withBlackBishop("f8")
                .withBlackPawns("a7","b7","c7","h7","g6")
                .build();

        var whiteKnight = board.getPiece("f5").get();
        var interImpacts = new ArrayList<>(
                board.getImpacts(whiteKnight, Impact.Type.INTERFERENCE)
        );

        assertFalse(interImpacts.isEmpty());
        assertEquals(2, interImpacts.size());

        var blackRook = board.getPiece("d8").get();
        var blackQueen = board.getPiece("d2").get();

        var positions = List.of(
                board.getPosition("d6").get(),
                board.getPosition("d4").get()
        );

        interImpacts.forEach(impact -> {
                var interImpact = (PieceInterferenceImpact<?,?,?,?,?>) impact;

                assertEquals(whiteKnight, interImpact.getInterferencor());
                assertEquals(blackRook,   interImpact.getProtector());
                assertEquals(blackQueen,  interImpact.getProtected());

                assertTrue(positions.contains(interImpact.getPosition()));
                assertFalse(interImpact.getLine().isEmpty());
            });
    }
}