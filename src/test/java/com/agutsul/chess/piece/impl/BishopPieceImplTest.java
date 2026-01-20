package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.piece.Piece.isBishop;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteDiscoveredAttackImpact;
import com.agutsul.chess.activity.impact.PieceAbsoluteForkImpact;
import com.agutsul.chess.activity.impact.PieceAbsoluteSkewerImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceBatteryImpact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PieceDeflectionAttackImpact;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.activity.impact.PieceRelativeDiscoveredAttackImpact;
import com.agutsul.chess.activity.impact.PieceRelativeSkewerImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeAttackImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeMoveImpact;
import com.agutsul.chess.activity.impact.PieceSkewerImpact;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.activity.impact.PieceXRayAttackImpact;
import com.agutsul.chess.activity.impact.PieceXRayImpact;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;

@ExtendWith(MockitoExtension.class)
public class BishopPieceImplTest extends AbstractPieceTest {

    private static final Type BISHOP_TYPE = Piece.Type.BISHOP;

    @Test
    void testDefaultBishopActionsOnStandardBoard() {
        var expectedPositions = List.of("c1", "f1", "c8", "f8");

        var board = new StandardBoard();
        var pieces = board.getPieces(BISHOP_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        assertPieceActions(board, Colors.WHITE, BISHOP_TYPE, expectedPositions.get(0));
        assertPieceActions(board, Colors.WHITE, BISHOP_TYPE, expectedPositions.get(1));

        assertPieceActions(board, Colors.BLACK, BISHOP_TYPE, expectedPositions.get(2));
        assertPieceActions(board, Colors.BLACK, BISHOP_TYPE, expectedPositions.get(3));
    }

    @Test
    void testDefaultBishopActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteBishop("c1").build();
        assertPieceActions(board1, Colors.WHITE, BISHOP_TYPE, "c1",
                List.of("b2", "a3", "d2", "e3", "f4", "g5", "h6"));

        var board2 = new LabeledBoardBuilder().withWhiteBishop("f1").build();
        assertPieceActions(board2, Colors.WHITE, BISHOP_TYPE, "f1",
                List.of("g2", "h3", "e2", "d3", "c4", "b5", "a6"));

        var board3 = new LabeledBoardBuilder().withBlackBishop("c8").build();
        assertPieceActions(board3, Colors.BLACK, BISHOP_TYPE, "c8",
                List.of("b7", "a6", "d7", "e6", "f5", "g4", "h3"));

        var board4 = new LabeledBoardBuilder().withBlackBishop("f8").build();
        assertPieceActions(board4, Colors.BLACK, BISHOP_TYPE, "f8",
                List.of("g7", "h6", "e7", "d6", "c5", "b4", "a3"));
    }

    @Test
    void testRandomBishopActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteBishop("d4").build();
        assertPieceActions(board1, Colors.WHITE, BISHOP_TYPE, "d4",
                List.of("c3", "b2", "a1", "e5", "f6", "g7", "h8", "e3", "f2", "g1", "c5", "b6", "a7"));

        var board2 = new LabeledBoardBuilder().withBlackBishop("d5").build();
        assertPieceActions(board2, Colors.BLACK, BISHOP_TYPE, "d5",
                List.of("e4", "f3", "g2", "h1", "c6", "b7", "a8", "e6", "f7", "g8", "c4", "b3", "a2"));
    }

    @Test
    void testBishopCaptureActionOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withBlackPawn("a3")
                .withWhiteBishop("c1")
                .build();

        assertPieceActions(board1, Colors.WHITE, BISHOP_TYPE, "c1",
                List.of("b2", "d2", "e3", "f4", "g5", "h6"), List.of("a3"));

        var board2 = new LabeledBoardBuilder()
                .withWhitePawn("h6")
                .withBlackBishop("f8")
                .build();

        assertPieceActions(board2, Colors.BLACK, BISHOP_TYPE, "f8",
                List.of("g7", "e7", "d6", "c5", "b4", "a3"), List.of("h6"));
    }

    @Test
    void testBishopActionAfterDisposing() {
        var board1 = new LabeledBoardBuilder()
                .withWhiteBishop("c1")
                .withWhitePawn("b2")
                .build();

        var whiteBishop = board1.getPiece("c1").get();
        assertFalse(board1.getActions(whiteBishop).isEmpty());
        assertFalse(board1.getImpacts(whiteBishop).isEmpty());

        ((BishopPiece<Color>) whiteBishop).dispose(null);

        assertTrue(board1.getActions(whiteBishop).isEmpty());
        assertTrue(board1.getImpacts(whiteBishop).isEmpty());

        var board2 = new LabeledBoardBuilder()
                .withBlackBishop("c8")
                .withBlackPawn("b7")
                .build();

        var blackBishop = board2.getPiece("c8").get();
        assertFalse(board2.getActions(blackBishop).isEmpty());
        assertFalse(board2.getImpacts(blackBishop).isEmpty());

        ((BishopPiece<Color>) blackBishop).dispose(null);

        assertTrue(board2.getActions(blackBishop).isEmpty());
        assertTrue(board2.getImpacts(blackBishop).isEmpty());
    }

    @Test
    void testBishopZeroActionForPinnedPiece() {
        var board = new LabeledBoardBuilder()
                .withWhiteBishop("e4")
                .withWhiteKing("e3")
                .withBlackRook("e8")
                .withBlackKing("c7")
                .build();

        var whiteBishop = board.getPiece("e4").get();
        var bishopActions = board.getActions(whiteBishop);
        assertTrue(bishopActions.isEmpty());
    }

    @Test
    void testBishopAbsoluteForkImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackQueen("d7")
                .withBlackRook("c8")
                .withBlackKnight("b6")
                .withBlackPawns("a6","b7","d5","f7","g6","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("b2")
                .withWhiteBishop("d4")
                .withWhiteRook("e1")
                .withWhitePawns("a2","b3","c2","f2","g2","h2")
                .build();

        var whiteBishop = board.getPiece("d4").get();
        var forkImpacts = board.getImpacts(whiteBishop, Impact.Type.FORK);
        assertFalse(forkImpacts.isEmpty());

        var absoluteForkImpacts = forkImpacts.stream()
                .map(impact -> (PieceForkImpact<?,?,?,?>) impact)
                .filter(PieceForkImpact::isAbsolute)
                .map(impact -> (PieceAbsoluteForkImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(absoluteForkImpacts.isEmpty());
        assertEquals(1, absoluteForkImpacts.size());

        var absoluteForkImpact = absoluteForkImpacts.getFirst();

        var forkedImpacts = new ArrayList<>(absoluteForkImpact.getTarget());
        assertEquals(whiteBishop, absoluteForkImpact.getSource());
        assertEquals(2, forkedImpacts.size());

        var blackKing = board.getPiece("h8").get();
        var impact1 = forkedImpacts.getFirst();
        assertEquals(blackKing, impact1.getTarget());
        assertEquals(blackKing.getPosition(), impact1.getPosition());

        var blackKnight = board.getPiece("b6").get();
        var impact2 = forkedImpacts.getLast();
        assertEquals(blackKnight, impact2.getTarget());
        assertEquals(blackKnight.getPosition(), impact2.getPosition());

        var impactTypes = List.of(Impact.Type.ATTACK, Impact.Type.CHECK);
        forkedImpacts.forEach(impact -> {
            assertTrue(impactTypes.contains(impact.getType()));
            assertTrue(isBishop(impact.getSource()));
            assertTrue(!impact.getLine().isEmpty());
        });
    }

    @Test
    void testBishopRelativeSkewerImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c7")
                .withBlackQueen("f7")
                .withBlackRook("g8")
                .withBlackKnight("g6")
                .withBlackBishop("c6")
                .withBlackPawns("a7","b6","f5")
                .withWhiteKing("e1")
                .withWhiteQueen("d1")
                .withWhiteRook("d4")
                .withWhiteBishops("c4","g5")
                .withWhitePawns("a3","e3","g2","h4")
                .build();

        var whiteBishop = board.getPiece("c4").get();
        var skewerImpacts = board.getImpacts(whiteBishop, Impact.Type.SKEWER);
        assertFalse(skewerImpacts.isEmpty());

        var relativeSkewerImpacts = skewerImpacts.stream()
                .map(impact -> (PieceSkewerImpact<?,?,?,?,?>) impact)
                .filter(PieceSkewerImpact::isRelative)
                .map(impact -> (PieceRelativeSkewerImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(relativeSkewerImpacts.isEmpty());

        assertEquals(1, relativeSkewerImpacts.size());

        var relativeSkewerImpact = relativeSkewerImpacts.getFirst();
        assertEquals(whiteBishop, relativeSkewerImpact.getAttacker());

        var blackQueen = board.getPiece("f7").get();
        assertEquals(blackQueen, relativeSkewerImpact.getAttacked());

        var blackRook = board.getPiece("g8").get();
        assertEquals(blackRook, relativeSkewerImpact.getDefended());
    }

    @Test
    void testBishopAbsoluteSkewerImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e7")
                .withBlackQueen("d7")
                .withBlackBishop("d5")
                .withWhiteKing("e4")
                .withWhiteQueen("f3")
                .withWhiteRook("f4")
                .build();

        var blackBishop = board.getPiece("d5").get();
        var skewerImpacts = board.getImpacts(blackBishop, Impact.Type.SKEWER);
        assertFalse(skewerImpacts.isEmpty());

        var absoluteSkewerImpacts = skewerImpacts.stream()
                .map(impact -> (PieceSkewerImpact<?,?,?,?,?>) impact)
                .filter(PieceSkewerImpact::isAbsolute)
                .map(impact -> (PieceAbsoluteSkewerImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(absoluteSkewerImpacts.isEmpty());

        assertEquals(1, absoluteSkewerImpacts.size());

        var absoluteSkewerImpact = absoluteSkewerImpacts.getFirst();
        assertEquals(blackBishop, absoluteSkewerImpact.getAttacker());

        var whiteKing = board.getPiece("e4").get();
        assertEquals(whiteKing, absoluteSkewerImpact.getAttacked());

        var whiteQueen = board.getPiece("f3").get();
        assertEquals(whiteQueen, absoluteSkewerImpact.getDefended());
    }

    @Test
    void testBishopNoRelativeSkewerImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c7")
                .withBlackRooks("g8","f7")
                .withBlackKnight("g6")
                .withBlackBishop("c6")
                .withBlackPawns("a7","b6","f5")
                .withWhiteKing("e1")
                .withWhiteQueen("d1")
                .withWhiteRook("d4")
                .withWhiteBishops("c4","g5")
                .withWhitePawns("a3","e3","g2","h4")
                .build();

        var whiteBishop = board.getPiece("c4").get();
        var skewerImpacts = board.getImpacts(whiteBishop, Impact.Type.SKEWER);
        assertTrue(skewerImpacts.isEmpty());
    }

    @Test
    void testBishopDiscoveredCheckImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("b2")
                .withWhiteRook("e3")
                .withWhiteBishop("e4")
                .withWhitePawn("g2")
                .withBlackKing("e5")
                .withBlackQueen("e8")
                .withBlackRook("g3")
                .build();

        var whiteBishop = board.getPiece("e4").get();
        var discoveredAttackImpacts = board.getImpacts(whiteBishop, Impact.Type.ATTACK);
        assertFalse(discoveredAttackImpacts.isEmpty());

        var absoluteDiscoveredAttackImpacts = discoveredAttackImpacts.stream()
                .map(impact -> (PieceDiscoveredAttackImpact<?,?,?,?,?>) impact)
                .filter(PieceDiscoveredAttackImpact::isAbsolute)
                .map(impact -> (PieceAbsoluteDiscoveredAttackImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(absoluteDiscoveredAttackImpacts.isEmpty());
        assertEquals(1, absoluteDiscoveredAttackImpacts.size());

        var absoluteDiscoveredAttackImpact = absoluteDiscoveredAttackImpacts.getFirst();
        assertEquals(whiteBishop, absoluteDiscoveredAttackImpact.getSource());

        var blackKing = board.getPiece("e5").get();

        var checkImpact = absoluteDiscoveredAttackImpact.getTarget();
        assertEquals(blackKing, checkImpact.getTarget());
        assertTrue(checkImpact.isHidden());

        var whiteRook = board.getPiece("e3").get();
        assertEquals(whiteRook, checkImpact.getSource());

        var line = checkImpact.getLine();

        assertTrue(line.isPresent());
        assertFalse(line.get().isEmpty());
    }

    @Test
    void testBishopDiscoveredAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("c1")
                .withWhiteRook("d1")
                .withWhiteBishop("d2")
                .withWhitePawns("a2","b2","c2")
                .withBlackKing("g8")
                .withBlackQueen("d6")
                .withBlackPawns("f7","g7","h7")
                .build();

        var whiteBishop = board.getPiece("d2").get();
        var discoveredAttackImpacts = board.getImpacts(whiteBishop, Impact.Type.ATTACK);
        assertFalse(discoveredAttackImpacts.isEmpty());

        var relativeDiscoveredAttackImpacts = discoveredAttackImpacts.stream()
                .map(impact -> (PieceDiscoveredAttackImpact<?,?,?,?,?>) impact)
                .filter(PieceDiscoveredAttackImpact::isRelative)
                .map(impact -> (PieceRelativeDiscoveredAttackImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(relativeDiscoveredAttackImpacts.isEmpty());
        assertEquals(1, relativeDiscoveredAttackImpacts.size());

        var relativeDiscoveredAttackImpact = relativeDiscoveredAttackImpacts.getFirst();
        assertEquals(whiteBishop, relativeDiscoveredAttackImpact.getSource());

        var blackQueen = board.getPiece("d6").get();

        var attackImpact = relativeDiscoveredAttackImpact.getTarget();
        assertEquals(blackQueen, attackImpact.getTarget());
        assertTrue(attackImpact.isHidden());

        var whiteRook = board.getPiece("d1").get();
        assertEquals(whiteRook, attackImpact.getSource());

        var line = attackImpact.getLine();

        assertTrue(line.isPresent());
        assertFalse(line.get().isEmpty());
    }

    @Test
    void testBishopUnderminingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnights("f7","h7")
                .withBlackPawn("g5")
                .withWhiteKing("e1")
                .withWhiteBishop("g6")
                .build();

        var whiteBishop = board.getPiece("g6").get();
        var underminingImpacts = board.getImpacts(whiteBishop, Impact.Type.UNDERMINING);

        assertFalse(underminingImpacts.isEmpty());
        assertEquals(2, underminingImpacts.size());

        var blackKnights = Stream.of(board.getPieces(Colors.BLACK, Piece.Type.KNIGHT))
                .flatMap(Collection::stream)
                .collect(toMap(Piece::getPosition, identity()));

        underminingImpacts.stream()
            .map(impact -> (PieceUnderminingImpact<?,?,?,?>) impact)
            .forEach(underminingImpact -> {
                assertEquals(whiteBishop, underminingImpact.getAttacker());
                assertTrue(blackKnights.values().contains(underminingImpact.getAttacked()));
                assertTrue(blackKnights.keySet().contains(underminingImpact.getPosition()));
                assertTrue(underminingImpact.getLine().isPresent());
            });
    }

    @Test
    void testBishopDeflectionImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("d8")
                .withBlackBishops("c8","f8")
                .withBlackKnights("b8","f6")
                .withBlackRooks("a8","h8")
                .withBlackPawns("a7","b7","e7","g6","h7")
                .withWhiteKing("e1")
                .withWhiteQueen("d1")
                .withWhiteBishops("c1","f7")
                .withWhiteKnights("c3","g1")
                .withWhiteRooks("a1","h1")
                .withWhitePawns("a2","b2","c2","c5","f2","g2","h2")
                .build();

        var whiteBishop = board.getPiece("f7").get();
        var deflectionImpacts = new ArrayList<>(
                board.getImpacts(whiteBishop, Impact.Type.DEFLECTION)
        );

        assertFalse(deflectionImpacts.isEmpty());
        assertEquals(1, deflectionImpacts.size());

        var deflectionImpact = (PieceDeflectionAttackImpact<?,?,?,?,?>) deflectionImpacts.getFirst();
        assertEquals(whiteBishop, deflectionImpact.getAttacker());

        var blackKing = board.getPiece("e8").get();
        assertEquals(blackKing, deflectionImpact.getAttacked());

        var blackQueen = board.getPiece("d8").get();
        assertEquals(blackQueen, deflectionImpact.getDefended());

        var attackImplact = deflectionImpact.getAttackImpact();

        assertNotNull(attackImplact);
        assertTrue(attackImplact instanceof PieceCheckImpact<?,?,?,?>);

        assertEquals(blackKing, attackImplact.getTarget());
        assertEquals(whiteBishop, attackImplact.getSource());
        assertTrue(attackImplact.getLine().isPresent());
    }

    @Test
    // https://www.chess.com/terms/battery-chess
    void testBishopBatteryImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d8")
                .withBlackRooks("c8","f8")
                .withBlackBishops("b7","e7")
                .withBlackKnights("d7","a5")
                .withBlackPawns("a6","b5","c7","e6","f7","g7","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("d3")
                .withWhiteRooks("c1","f1")
                .withWhiteBishops("b1","f4")
                .withWhiteKnights("f3","c3")
                .withWhitePawns("a3","b2","d4","e3","f2","g2","h3")
                .build();

        var whiteBishop1 = board.getPiece("b1").get();
        var batteryImpacts1 = new ArrayList<>(
                board.getImpacts(whiteBishop1, Impact.Type.BATTERY)
        );

        assertFalse(batteryImpacts1.isEmpty());
        assertEquals(1, batteryImpacts1.size());

        var whiteQueen = board.getPiece("d3").get();

        var batteryImpact1 = (PieceBatteryImpact<?,?,?>) batteryImpacts1.getFirst();
        assertEquals(whiteQueen, batteryImpact1.getTarget());

        var line1 = batteryImpact1.getLine();
        assertFalse(line1.isEmpty());

        var blackPawnPosition = board.getPosition("h7").get();
        assertEquals(blackPawnPosition, line1.getLast());
    }

    @Test
    void testBishopNoBatteryImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d8")
                .withBlackRooks("f8","g8")
                .withBlackBishop("e7")
                .withBlackKnights("c6","f6")
                .withBlackPawns("a7","b7","c7","e6","f7","g7","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("h4")
                .withWhiteRooks("a1","f3")
                .withWhiteBishops("e3","e4")
                .withWhiteKnight("c3")
                .withWhitePawns("a2","b2","c2","d4","g2","h2")
                .build();

        var whiteQueen = board.getPiece("h4").get();
        var queenBatteryImpacts = board.getImpacts(whiteQueen, Impact.Type.BATTERY);
        assertTrue(queenBatteryImpacts.isEmpty());

        var whiteBishop1 = board.getPiece("e3").get();
        var bishop1BatteryImpacts = board.getImpacts(whiteBishop1, Impact.Type.BATTERY);
        assertTrue(bishop1BatteryImpacts.isEmpty());

        var whiteBishop2 = board.getPiece("e4").get();
        var bishop2BatteryImpacts = board.getImpacts(whiteBishop2, Impact.Type.BATTERY);
        assertTrue(bishop2BatteryImpacts.isEmpty());

        var whiteRook1 = board.getPiece("a1").get();
        var rook1BatteryImpacts = board.getImpacts(whiteRook1, Impact.Type.BATTERY);
        assertTrue(rook1BatteryImpacts.isEmpty());

        var whiteRook2 = board.getPiece("f3").get();
        var rook2BatteryImpacts = board.getImpacts(whiteRook2, Impact.Type.BATTERY);
        assertTrue(rook2BatteryImpacts.isEmpty());
    }

    @Test
    void testBishopOutpostImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g6")
                .withBlackRooks("b5","e6")
                .withBlackKnight("e8")
                .withBlackPawns("a6","b4","c5","e5","f6","g5","h6")
                .withWhiteKing("f2")
                .withWhiteRooks("a7","d8")
                .withWhiteBishop("e2")
                .withWhitePawns("a5","b3","c2","f3","g2","h3")
                .build();

        var whiteBishop = board.getPiece("e2").get();
        var outpostImpacts = new ArrayList<>(
                board.getImpacts(whiteBishop, Impact.Type.OUTPOST)
        );

        assertFalse(outpostImpacts.isEmpty());
        assertEquals(1, outpostImpacts.size());

        var outpostImpact = outpostImpacts.getFirst();
        assertEquals(board.getPosition("c4").get(), outpostImpact.getPosition());
    }

    @Test
    // https://www.chess.com/forum/view/chess-openings/bishop-knight-sacrifice-gambits
    void testBishopSacrificeImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("d8")
                .withBlackRooks("a8","h8")
                .withBlackBishops("c8","f8")
                .withBlackKnights("c6","e4")
                .withBlackPawns("a7","b7","c7","d7","e5","f7","g7","h7")
                .withWhiteKing("e1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","h1")
                .withWhiteBishops("c1","c4")
                .withWhiteKnights("c3","f3")
                .withWhitePawns("a2","b2","c2","d2","f2","g2","h2")
                .build();

        var whiteBishop = board.getPiece("c4").get();
        var sacrificeImpacts = board.getImpacts(whiteBishop, Impact.Type.SACRIFICE);

        assertFalse(sacrificeImpacts.isEmpty());
        assertEquals(4, sacrificeImpacts.size());

        var blackPawn1 = board.getPiece("d7").get();
        var blackPawn2 = board.getPiece("f7").get();
        var blackPawn3 = board.getPiece("b7").get();

        var blackPawns = List.of(blackPawn1, blackPawn2, blackPawn3);
        var emptyPositions = Stream.of("e6","a6")
                .map(position -> board.getPosition(position))
                .flatMap(Optional::stream)
                .toList();

        var sacrificeMoveImpacts = sacrificeImpacts.stream()
                .filter(impact -> impact instanceof PieceSacrificeMoveImpact)
                .map(impact -> (PieceSacrificeMoveImpact<?,?,?,?>) impact)
                .toList();

        assertEquals(3, sacrificeMoveImpacts.size());

        sacrificeMoveImpacts.stream().forEach(impact -> {
            assertEquals(whiteBishop, impact.getSacrificed());
            assertTrue(blackPawns.contains(impact.getAttacker()));
            assertTrue(emptyPositions.contains(impact.getPosition()));
            assertTrue(emptyPositions.contains(impact.getSource().getTarget()));
        });

        var blackKing = board.getPiece("e8").get();

        var sacrificeAttackImpacts = sacrificeImpacts.stream()
                .filter(impact -> impact instanceof PieceSacrificeAttackImpact)
                .map(impact -> (PieceSacrificeAttackImpact<?,?,?,?,?>) impact)
                .toList();

        assertEquals(1, sacrificeAttackImpacts.size());

        var impact = sacrificeAttackImpacts.getFirst();

        assertEquals(whiteBishop, impact.getSacrificed());
        assertEquals(blackKing, impact.getAttacker());
        assertEquals(blackPawn2.getPosition(), impact.getPosition());
        assertEquals(blackPawn2, impact.getSource().getTarget());
    }

    @Test
    // https://en.wikipedia.org/wiki/X-ray_(chess)
    void testBishopXRayProtectImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d8")
                .withBlackRooks("a8","e8")
                .withBlackKnights("b8","e7")
                .withBlackBishop("d4")
                .withBlackPawns("b4","b7","c6","f7","g6")
                .withWhiteKing("c1")
                .withWhiteQueen("h8")
                .withWhiteRooks("d1","h1")
                .withWhiteBishops("b2","f1")
                .withWhitePawns("a2","c4","d2","f2","g2")
                .build();

        var whiteBishop = board.getPiece("b2").get();
        var xRayImpacts = new ArrayList<>(
                board.getImpacts(whiteBishop, Impact.Type.XRAY)
        );

        assertFalse(xRayImpacts.isEmpty());
        assertEquals(1, xRayImpacts.size());

        var xRayImpact = (PieceXRayImpact<?,?,?,?>) xRayImpacts.getFirst();
        assertTrue(PieceXRayImpact.isRelative(xRayImpact));

        var whiteQueen = board.getPiece("h8").get();

        assertEquals(whiteQueen,  xRayImpact.getTarget());
        assertEquals(whiteBishop, xRayImpact.getPiece());
        assertNotNull(xRayImpact.getLine());

        var blackBishop = board.getPiece("d4").get();

        var pieces = xRayImpact.getPieces();
        assertFalse(pieces.isEmpty());
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(blackBishop));

        var originImpact = xRayImpact.getSource();
        assertTrue(Impact.isProtect((Impact<?>) originImpact));

        var protectImpact = (PieceProtectImpact<?,?,?>) originImpact;
        assertTrue(protectImpact.isHidden());
    }

    @Test
    // https://en.wikipedia.org/wiki/X-ray_(chess)
    void testBishopXRayAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackQueen("d8")
                .withBlackRooks("b8","f8")
                .withBlackBishops("c8","h4")
                .withBlackKnight("c6")
                .withBlackPawns("a5","d6","f7","g6","h7")
                .withWhiteKing("f1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a4","h1")
                .withWhiteBishop("c4")
                .withWhiteKnights("d5","e3")
                .withWhitePawns("b3","c3","e4","f4")
                .build();

        var whiteBishop = board.getPiece("c4").get();
        var xRayImpacts = board.getImpacts(whiteBishop, Impact.Type.XRAY);

        assertFalse(xRayImpacts.isEmpty());
        assertEquals(1, xRayImpacts.size());

        var relativeXRayImpacts = Stream.of(xRayImpacts)
                .flatMap(Collection::stream)
                .map(impact -> (PieceXRayImpact<?,?,?,?>) impact)
                .filter(PieceXRayImpact::isRelative)
                .toList();

        assertEquals(1, relativeXRayImpacts.size());

        var blackPawn = board.getPiece("f7").get();
        var whiteKnight = board.getPiece("d5").get();

        var xRayImpact = relativeXRayImpacts.get(0);

        var source = xRayImpact.getSource();
        assertTrue(Impact.isAttack((Impact<?>) source));
        assertTrue(((PieceAttackImpact<?,?,?,?>) source).isHidden());

        assertEquals(whiteBishop, xRayImpact.getPiece());
        assertEquals(blackPawn, xRayImpact.getTarget());
        assertTrue(xRayImpact.getPieces().contains(whiteKnight));
        assertTrue(xRayImpact instanceof PieceXRayAttackImpact);
        assertNotNull(xRayImpact.getLine());
    }
}