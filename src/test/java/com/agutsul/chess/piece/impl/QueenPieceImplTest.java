package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.impact.Impact.isAttack;
import static com.agutsul.chess.piece.Piece.isQueen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceBatteryImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PieceOverloadingImpact;
import com.agutsul.chess.activity.impact.PiecePartialPinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativeDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceRelativeForkImpact;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
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

        var forkedImpacts = new ArrayList<>(relativeForkImpact.getTarget());
        assertEquals(whiteQueen, relativeForkImpact.getSource());
        assertEquals(3, forkedImpacts.size());

        var blackKnight = board.getPiece("b6").get();
        var impact1 = forkedImpacts.getFirst();
        assertEquals(blackKnight, impact1.getTarget());
        assertEquals(blackKnight.getPosition(), impact1.getPosition());

        var blackPawn = board.getPiece("g7").get();
        var impact2 = forkedImpacts.getLast();
        assertEquals(blackPawn, impact2.getTarget());
        assertEquals(blackPawn.getPosition(), impact2.getPosition());

        forkedImpacts.forEach(impact -> {
            assertTrue(isAttack(impact));
            assertTrue(isQueen(impact.getSource()));
            assertFalse(impact.getLine().isEmpty());
        });
    }

    @Test
    // https://www.chess.com/blog/Win_Like_McEntee/removing-the-defender
    void testQueenUnderminingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("f8")
                .withBlackKnight("f2")
                .withBlackPawns("a7","g7","b6","h6")
                .withWhiteKing("h2")
                .withWhiteQueen("d6")
                .withWhiteRook("b2")
                .withWhitePawns("g2","h3")
                .build();

        var whiteQueen = board.getPiece("d6").get();
        var underminingImpacts = new ArrayList<>(
                board.getImpacts(whiteQueen, Impact.Type.UNDERMINING)
        );

        assertFalse(underminingImpacts.isEmpty());
        assertEquals(1, underminingImpacts.size());

        var underminingImpact = (PieceUnderminingImpact<?,?,?,?>) underminingImpacts.getFirst();
        assertEquals(whiteQueen, underminingImpact.getAttacker());

        var blackQueen = board.getPiece("f8").get();
        assertEquals(blackQueen, underminingImpact.getAttacked());
        assertEquals(blackQueen.getPosition(), underminingImpact.getPosition());

        assertTrue(underminingImpact.getLine().isPresent());
    }

    @Test
    // https://www.chess.com/terms/overloading-chess
    void testQueenOverloadingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackQueen("d8")
                .withBlackRook("f2")
                .withBlackBishop("e7")
                .withBlackPawns("a7","b7","g7","h7","h2")
                .withWhiteKing("h1")
                .withWhiteQueen("g4")
                .withWhiteRook("e4")
                .withWhiteBishop("f4")
                .withWhitePawns("a2","b4","c3","c5","g2")
                .build();

        var blackQueen = board.getPiece("d8").get();
        var overloadingImpacts = board.getImpacts(blackQueen, Impact.Type.OVERLOADING);

        assertFalse(overloadingImpacts.isEmpty());
        assertEquals(11, overloadingImpacts.size());

        var expectedPositions = List.of("d4","a5","b6","c7","b8","d6","c8","d2","d7","d1","e7");
        var overloadedPositions = Stream.of(overloadingImpacts)
                .flatMap(Collection::stream)
                .map(impact -> (PieceOverloadingImpact<?,?>) impact)
                .map(PieceOverloadingImpact::getPosition)
                .map(String::valueOf)
                .collect(toSet());

        assertEquals(expectedPositions.size(), overloadedPositions.size());
        assertTrue(overloadedPositions.containsAll(expectedPositions));
    }

    @Test
    // https://en.wikipedia.org/wiki/Battery_(chess)
    void testQueenBatteryImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("d8")
                .withBlackRooks("a8","h8")
                .withBlackBishop("g7")
                .withBlackKnights("b8","f6")
                .withBlackPawns("a7","b7","d6","e7","f7","g6","h7")
                .withWhiteKing("c1")
                .withWhiteQueen("e2")
                .withWhiteRooks("e1","e3")
                .withWhiteBishop("g2")
                .withWhiteKnights("c3","f3")
                .withWhitePawns("a2","b2","c2","d3","f4","g3","h2")
                .build();

        var whiteQueen = board.getPiece("e2").get();
        var batteryImpacts = board.getImpacts(whiteQueen, Impact.Type.BATTERY);

        assertFalse(batteryImpacts.isEmpty());
        assertEquals(2, batteryImpacts.size());

        var whiteRook1 = board.getPiece("e1").get();
        var whiteRook2 = board.getPiece("e3").get();

        var targetPieces = Stream.of(batteryImpacts)
                .flatMap(Collection::stream)
                .map(impact -> (PieceBatteryImpact<?,?,?>) impact)
                .map(PieceBatteryImpact::getTarget)
                .toList();

        assertTrue(targetPieces.containsAll(List.of(whiteRook1, whiteRook2)));

        var targetLines = Stream.of(batteryImpacts)
                .flatMap(Collection::stream)
                .map(impact -> (PieceBatteryImpact<?,?,?>) impact)
                .map(PieceBatteryImpact::getLine)
                .collect(Collectors.toSet());

        assertFalse(targetLines.isEmpty());
        assertEquals(1, targetLines.size());

        var line = targetLines.iterator().next();

        var blackKingPosition = board.getPosition("e8").get();
        assertEquals(blackKingPosition, line.getLast());
    }

    @Test
    // https://www.chess.com/terms/desperado-chess
    void testQueenRelativeDesperadoImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d2")
                .withBlackRook("a8")
                .withBlackBishop("g7")
                .withBlackPawns("a7","b7","c6","e5","f5","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("g4")
                .withWhiteRook("d1")
                .withWhiteBishop("g2")
                .withWhitePawns("a2","b3","c4","d3","g3","h2")
                .build();

        var whiteQueen = board.getPiece("g4").get();
        var desperadoImpacts = board.getImpacts(whiteQueen, Impact.Type.DESPERADO);

        assertFalse(desperadoImpacts.isEmpty());
        assertEquals(2, desperadoImpacts.size());

        var relativeDesperadoImpacts = Stream.of(desperadoImpacts)
                .flatMap(Collection::stream)
                .map(impact -> (PieceDesperadoImpact<?,?,?,?,?,?>) impact)
                .filter(PieceDesperadoImpact::isRelative)
                .map(impact -> (PieceRelativeDesperadoImpact<?,?,?,?,?,?>) impact)
                .toList();

        assertEquals(1, relativeDesperadoImpacts.size());

        var blackBishop = board.getPiece("g7").get();
        var blackKing   = board.getPiece("g8").get();
        var blackQueen  = board.getPiece("d2").get();
        var whiteRook   = board.getPiece("d1").get();

        var relativeImpact = relativeDesperadoImpacts.getFirst();
        var impacts = new ArrayList<>(relativeImpact.getTarget());

        var desperadoImpact = impacts.getFirst();
        assertEquals(whiteQueen,  desperadoImpact.getDesperado());
        assertEquals(blackBishop, desperadoImpact.getAttacked());
        assertEquals(blackKing,   desperadoImpact.getAttacker());

        var exchangeImpact = impacts.getLast();
        assertEquals(whiteRook,  exchangeImpact.getDesperado());
        assertEquals(blackQueen, exchangeImpact.getAttacked());
    }
}