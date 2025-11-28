package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.piece.Piece.isKnight;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteForkImpact;
import com.agutsul.chess.activity.impact.PieceAbsolutePinImpact;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativeDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceRelativePinImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeAttackImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeMoveImpact;
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

        var blackKing = board.getPiece("d7").get();
        var impact1 = forkedImpacts.getFirst();
        assertEquals(blackKing, impact1.getTarget());
        assertEquals(blackKing.getPosition(), impact1.getPosition());

        var blackRook = board.getPiece("a8").get();
        var impact2 = forkedImpacts.getLast();
        assertEquals(blackRook, impact2.getTarget());
        assertEquals(blackRook.getPosition(), impact2.getPosition());

        var impactTypes = List.of(Impact.Type.ATTACK, Impact.Type.CHECK);
        forkedImpacts.forEach(impact -> {
            assertTrue(impactTypes.contains(impact.getType()));
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

    @Test
    void testKnightOutpostImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g6")
                .withBlackRooks("b5","e6")
                .withBlackKnight("e8")
                .withBlackPawns("a6","b4","c5","e5","f6","g5","h6")
                .withWhiteKing("f2")
                .withWhiteRooks("a7","d8")
                .withWhiteKnight("e3")
                .withWhitePawns("a5","b3","c2","f3","g2","h3")
                .build();

        var whiteKnight = board.getPiece("e3").get();
        var outpostImpacts = new ArrayList<>(
                board.getImpacts(whiteKnight, Impact.Type.OUTPOST)
        );

        assertFalse(outpostImpacts.isEmpty());
        assertEquals(1, outpostImpacts.size());

        var outpostImpact = outpostImpacts.getFirst();
        assertEquals(board.getPosition("c4").get(), outpostImpact.getPosition());
    }

    @Test
    // https://www.chess.com/forum/view/chess-openings/bishop-knight-sacrifice-gambits
    void testKnightSacrificeImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("d8")
                .withBlackRooks("a8","h8")
                .withBlackBishops("c8","f8")
                .withBlackKnights("c6","f6")
                .withBlackPawns("a7","b7","c7","d7","e5","f7","g7","h7")
                .withWhiteKing("e1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","h1")
                .withWhiteBishops("c1","f1")
                .withWhiteKnights("c3","f3")
                .withWhitePawns("a2","b2","c2","d2","e4","f2","g2","h2")
                .build();

        var whiteKnight = board.getPiece("f3").get();
        var sacrificeImpacts = board.getImpacts(whiteKnight, Impact.Type.SACRIFICE);

        assertFalse(sacrificeImpacts.isEmpty());
        assertEquals(3, sacrificeImpacts.size());

        var emptyPosition = board.getPosition("d4").get();

        var blackKnight = board.getPiece("c6").get();
        var blackPawn = board.getPiece("e5").get();

        var attackers = List.of(blackKnight, blackPawn);

        var sacrificeMoveImpacts = sacrificeImpacts.stream()
                .filter(impact -> impact instanceof PieceSacrificeMoveImpact)
                .map(impact -> (PieceSacrificeMoveImpact<?,?,?,?>) impact)
                .toList();

        sacrificeMoveImpacts.stream().forEach(impact -> {
            assertEquals(whiteKnight, impact.getSacrificed());
            assertTrue(attackers.contains(impact.getAttacker()));
            assertEquals(emptyPosition, impact.getPosition());
            assertEquals(emptyPosition, impact.getSource().getTarget());
        });

        var sacrificeAttackImpacts = sacrificeImpacts.stream()
                .filter(impact -> impact instanceof PieceSacrificeAttackImpact)
                .map(impact -> (PieceSacrificeAttackImpact<?,?,?,?,?>) impact)
                .toList();

        assertEquals(1, sacrificeAttackImpacts.size());

        var impact = sacrificeAttackImpacts.getFirst();

        assertEquals(whiteKnight, impact.getSacrificed());
        assertEquals(blackKnight, impact.getAttacker());
        assertEquals(blackPawn.getPosition(), impact.getPosition());
        assertEquals(blackPawn, impact.getSource().getTarget());
    }

    @Test
    // https://www.chess.com/terms/desperado-chess
    void testKnightAbsoluteDesperadoImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("d8")
                .withBlackQueen("g6")
                .withBlackRook("h8")
                .withBlackBishops("b7","c5")
                .withBlackKnights("c6","g8")
                .withBlackPawns("a7","b6","d7","e5","f7","g7","h7")
                .withWhiteKing("e1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","h1")
                .withWhiteBishops("c1","c4")
                .withWhiteKnights("a8","f3")
                .withWhitePawns("a2","b2","c2","d3","e4","f2","g2","h2")
                .build();

        var whiteKnight = board.getPiece("a8").get();
        var desperadoImpacts = board.getImpacts(whiteKnight, Impact.Type.DESPERADO);

        assertFalse(desperadoImpacts.isEmpty());
        assertEquals(2, desperadoImpacts.size());

        var blackPawn1  = board.getPiece("b6").get();
        var blackPawn2  = board.getPiece("a7").get();
        var blackBishop = board.getPiece("c5").get();

        var attackers = List.of(blackPawn2, blackBishop);
        desperadoImpacts.stream()
            .map(impact -> (PieceDesperadoImpact<?,?,?,?,?,?>) impact)
            .forEach(impact -> {
                assertEquals(blackPawn1,  impact.getAttacked());
                assertEquals(whiteKnight, impact.getDesperado());
                assertTrue(attackers.contains(impact.getAttacker()));
                assertTrue(PieceDesperadoImpact.isAbsolute(impact));
            });
    }

    @Test
    // https://en.wikipedia.org/wiki/Desperado_(chess)
    void testKnightRelativeDesperadoImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d8")
                .withBlackRooks("a8","f8")
                .withBlackBishops("c8","g7")
                .withBlackKnights("e7","h5")
                .withBlackPawns("a7","b7","c7","d6","f5","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","f1")
                .withWhiteBishops("c1","g2")
                .withWhiteKnights("c3","e5")
                .withWhitePawns("a2","b2","c4","d5","f2","g3","h2")
                .build();

        var blackKnight = board.getPiece("h5").get();
        var desperadoImpacts = board.getImpacts(blackKnight, Impact.Type.DESPERADO);

        assertFalse(desperadoImpacts.isEmpty());
        assertEquals(6, desperadoImpacts.size());

        var relativeDesperadoImpacts = Stream.of(desperadoImpacts)
                .flatMap(Collection::stream)
                .map(impact -> (PieceDesperadoImpact<?,?,?,?,?,?>) impact)
                .filter(PieceDesperadoImpact::isRelative)
                .map(impact -> (PieceRelativeDesperadoImpact<?,?,?,?,?,?>) impact)
                .toList();

        assertEquals(4, relativeDesperadoImpacts.size());

        var whitePawn1  = board.getPiece("h2").get();
        var whitePawn2  = board.getPiece("f2").get();
        var whitePawn3  = board.getPiece("g3").get();
        var whiteKnight = board.getPiece("e5").get();

        var blackBishop = board.getPiece("g7").get();
        var blackPawn   = board.getPiece("d6").get();

        var attackers = List.of(whitePawn1, whitePawn2);
        var pieces = List.of(blackBishop, blackPawn);

        for (var relativeImpact : relativeDesperadoImpacts) {
            var impacts = new ArrayList<>(relativeImpact.getTarget());

            var desperadoImpact = impacts.getFirst();
            assertEquals(blackKnight, desperadoImpact.getDesperado());
            assertEquals(whitePawn3,  desperadoImpact.getAttacked());
            assertTrue(attackers.contains(desperadoImpact.getAttacker()));

            var exchangeImpact = impacts.getLast();
            assertTrue(pieces.contains(exchangeImpact.getDesperado()));
            assertEquals(whiteKnight,  exchangeImpact.getAttacked());
        }
    }
}