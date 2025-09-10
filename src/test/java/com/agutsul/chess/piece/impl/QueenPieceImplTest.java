package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.piece.Piece.isQueen;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PiecePartialPinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativeForkImpact;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;

@ExtendWith(MockitoExtension.class)
public class QueenPieceImplTest extends AbstractPieceTest {

    private static final Type QUEEN_TYPE = Piece.Type.QUEEN;

    @Test
    void testDefaultQueenActionsOnStandardBoard() {
        var expectedPositions = List.of("d1", "d8");

        var board = new StandardBoard();
        var pieces = board.getPieces(QUEEN_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        assertPieceActions(board, Colors.WHITE, QUEEN_TYPE, expectedPositions.get(0));
        assertPieceActions(board, Colors.BLACK, QUEEN_TYPE, expectedPositions.get(1));
    }

    @Test
    void testDefaultQueenActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteQueen("d1").build();
        assertPieceActions(board1, Colors.WHITE, QUEEN_TYPE, "d1", List.of(
                        "d2", "d3", "d4", "d5", "d6", "d7", "d8",
                        "a1", "b1", "c1", "e1", "f1", "g1", "h1",
                        "c2", "b3", "a4",
                        "e2", "f3", "g4", "h5"
                        )
                );

        var board2 = new LabeledBoardBuilder().withBlackQueen("d8").build();
        assertPieceActions(board2, Colors.BLACK, QUEEN_TYPE, "d8", List.of(
                        "a8", "b8", "c8", "e8", "f8", "g8", "h8",
                        "d1", "d2", "d3", "d4", "d5", "d6", "d7",
                        "c7", "b6", "a5",
                        "e7", "f6", "g5", "h4"
                        )
                );
    }

    @Test
    void testRandomQueenActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteQueen("e4").build();
        assertPieceActions(board1, Colors.WHITE, QUEEN_TYPE, "e4", List.of(
                "e3", "e2", "e1",
                "e5", "e6", "e7", "e8",
                "a4", "b4", "c4", "d4",
                "f4", "g4", "h4",
                "d3", "c2", "b1",
                "f5", "g6", "h7",
                "d5", "c6", "b7", "a8",
                "f3", "g2", "h1"
            ));

        var board2 = new LabeledBoardBuilder().withBlackQueen("d5").build();
        assertPieceActions(board2, Colors.BLACK, QUEEN_TYPE, "d5", List.of(
                "d1", "d2", "d3", "d4", "d6", "d7", "d8",
                "a5", "b5", "c5", "e5", "f5", "g5", "h5",
                "e6", "f7", "g8",
                "c6", "b7", "a8",
                "c4", "b3", "a2",
                "e4", "f3", "g2", "h1"
            ));
    }

    @Test
    void testQueenCaptureActionOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withBlackPawn("a3")
                .withWhiteQueen("c1")
                .build();

        assertPieceActions(board1, Colors.WHITE, QUEEN_TYPE, "c1",
                List.of("b2", "d2", "e3", "f4", "g5", "h6", "c2", "c3", "c4", "g1",
                        "c5", "c6", "c7", "c8", "a1", "b1", "d1", "e1", "f1", "h1"),
                List.of("a3"));

        var board2 = new LabeledBoardBuilder()
                .withWhitePawn("h6")
                .withBlackQueen("f8")
                .build();

        assertPieceActions(board2, Colors.BLACK, QUEEN_TYPE, "f8",
                List.of("g7", "e7", "d6", "c5", "b4", "a3", "f7", "f6", "f5", "g8",
                        "f4", "f3", "f2", "f1", "a8", "b8", "c8", "d8", "e8", "h8"),
                List.of("h6"));
    }

    @Test
    void testQueenPartialPinImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("f1")
                .withWhiteRook("e1")
                .withWhiteBishop("b2")
                .withWhitePawn("d3")
                .withBlackKing("e8")
                .withBlackQueen("e4")
                .withBlackBishop("c4")
                .withBlackPawn("b7")
                .build();

        var blackQueen = board.getPiece("e4").get();
        var pinImpacts  = board.getImpacts(blackQueen, Impact.Type.PIN);
        assertFalse(pinImpacts.isEmpty());

        var partialPinImpacts = pinImpacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .filter(PiecePinImpact::isPartial)
                .map(impact -> (PiecePartialPinImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(partialPinImpacts.isEmpty());

        var partialPinImpact = partialPinImpacts.getFirst();
        assertTrue(partialPinImpact.isMode(PiecePinImpact.Mode.ABSOLUTE));

        var blackKing = board.getPiece("e8").get();
        var whiteRook = board.getPiece("e1").get();

        assertEquals(blackKing, partialPinImpact.getDefended());
        assertEquals(whiteRook, partialPinImpact.getAttacker());
    }

    @Test
    void testQueenRelativeForkImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d7")
                .withBlackRook("c8")
                .withBlackKnight("b6")
                .withBlackPawns("a6","b7","d5","f7","g7","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("d4")
                .withWhiteBishop("b2")
                .withWhiteRook("e1")
                .withWhitePawns("a2","b3","c2","f2","g2","h2")
                .build();

        var whiteQueen = board.getPiece("d4").get();
        var forkImpacts = board.getImpacts(whiteQueen, Impact.Type.FORK);
        assertFalse(forkImpacts.isEmpty());

        var relativeForkImpacts = forkImpacts.stream()
                .map(impact -> (PieceForkImpact<?,?,?,?>) impact)
                .filter(PieceForkImpact::isRelative)
                .map(impact -> (PieceRelativeForkImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(relativeForkImpacts.isEmpty());

        assertEquals(1, relativeForkImpacts.size());

        var relativeForkImpact = relativeForkImpacts.getFirst();

        var forkedImpacts = relativeForkImpact.getTarget();
        assertEquals(whiteQueen, relativeForkImpact.getSource());
        assertEquals(3, forkedImpacts.size());

        var attackedPieceTypes = List.of(Piece.Type.PAWN, Piece.Type.KNIGHT);
        forkedImpacts.forEach(impact -> {
            assertTrue(Impact.Type.ATTACK.equals(impact.getType()));
            assertEquals(whiteQueen.getPosition(), impact.getPosition());

            assertTrue(isQueen(impact.getSource()));
            assertTrue(attackedPieceTypes.contains(impact.getTarget().getType()));

            assertTrue(!impact.getLine().isEmpty());
        });
    }
}