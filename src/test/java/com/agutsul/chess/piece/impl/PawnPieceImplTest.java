package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.impact.Impact.isAttack;
import static com.agutsul.chess.piece.Piece.isPawn;
import static com.agutsul.chess.piece.Piece.isQueen;
import static com.agutsul.chess.piece.Piece.isRook;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteDiscoveredAttackImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PieceLuftImpact;
import com.agutsul.chess.activity.impact.PieceOverloadingImpact;
import com.agutsul.chess.activity.impact.PiecePartialPinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativeDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceRelativeForkImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeAttackImpact;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;

@ExtendWith(MockitoExtension.class)
public class PawnPieceImplTest extends AbstractPieceTest {

    private static final Type PAWN_TYPE = Piece.Type.PAWN;

    @Test
    void testDefaultPawnActionsOnStandardBoard() {
        var expectedPositions = List.of(
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", // white pawns
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"  // black pawns
        );

        var board = new StandardBoard();
        var pieces = board.getPieces(PAWN_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        Stream.of(Colors.values())
            .flatMap(color -> pieces.stream().filter(piece -> Objects.equals(piece.getColor(), color)))
            .forEach(piece -> {
                var position = piece.getPosition();
                var expected = Stream.of(
                            positionOf(position.x(), position.y() + piece.getDirection()),
                            positionOf(position.x(), position.y() + 2 * piece.getDirection()))
                    .map(String::valueOf)
                    .toList();

                assertTrue(expectedPositions.contains(String.valueOf(position)));
                assertPieceActions(board, piece.getColor(),
                        PAWN_TYPE, String.valueOf(position), expected
                );
            });
    }

    @Test
    void testRandomPawnActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhitePawn("d4").build();
        assertPieceActions(board1, Colors.WHITE, PAWN_TYPE, "d4", List.of("d5"));

        var board2 = new LabeledBoardBuilder().withBlackPawn("c5").build();
        assertPieceActions(board2, Colors.BLACK, PAWN_TYPE, "c5", List.of("c4"));
    }

    @Test
    void testPawnCaptureActionOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withBlackPawn("a5")
                .withWhitePawn("b4")
                .build();

        assertPieceActions(board1, Colors.WHITE, PAWN_TYPE, "b4", List.of("b5"), List.of("a5"));

        var board2 = new LabeledBoardBuilder()
                .withWhitePawn("d5")
                .withBlackPawn("e6")
                .build();

        assertPieceActions(board2, Colors.BLACK, PAWN_TYPE, "e6", List.of("e5"), List.of("d5"));
    }

    @Test
    void testPawnMovePromotionActionOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhitePawn("c7").build();
        assertPawnPromotionActions(board1, Colors.WHITE, PAWN_TYPE, "c7", List.of("c8"), List.of());

        var board2 = new LabeledBoardBuilder().withBlackPawn("b2").build();
        assertPawnPromotionActions(board2, Colors.BLACK, PAWN_TYPE, "b2", List.of("b1"), List.of());
    }

    @Test
    void testPawnCapturePromotionActionOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withWhitePawn("c7")
                .withBlackKnight("b8")
                .build();

        assertPawnPromotionActions(board1, Colors.WHITE, PAWN_TYPE, "c7",
                List.of("c8"), List.of("b8"));

        var board2 = new LabeledBoardBuilder()
                .withBlackPawn("c2")
                .withWhiteKnight("b1")
                .build();

        assertPawnPromotionActions(board2, Colors.BLACK, PAWN_TYPE, "c2",
                List.of("c1"), List.of("b1"));
    }

    @Test
    void testPawnEnPassantActionOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board1.getPiece("a7").get();
        blackPawn.move(board1.getPosition("a5").get());

        ((Observable) board1).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        assertPawnEnPassantActions(board1, Colors.WHITE, PAWN_TYPE, "b5",
                List.of("b6"), List.of("a6"));

        var board2 = new LabeledBoardBuilder()
                .withBlackPawn("b4")
                .withWhitePawn("a2")
                .build();

        var whitePawn = (PawnPiece<Color>) board2.getPiece("a2").get();
        whitePawn.move(board2.getPosition("a4").get());

        ((Observable) board2).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertPawnEnPassantActions(board2, Colors.BLACK, PAWN_TYPE, "b4",
                List.of("b3"), List.of("a3"));
    }

    @Test
    void testPawnMoveAction() {
        var board = new StandardBoard();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();
        var targetPosition = board.getPosition("e3").get();

        assertEquals(whitePawn.getPosition(), board.getPosition("e2").get());
        assertFalse(whitePawn.isMoved());

        whitePawn.move(targetPosition);

        assertEquals(whitePawn.getPosition(), board.getPosition("e3").get());
        assertTrue(whitePawn.isMoved());
    }

    @Test
    void testPawnBigMoveAction() {
        var board = new StandardBoard();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();
        var targetPosition = board.getPosition("e4").get();

        assertEquals(board.getPosition("e2").get(), whitePawn.getPosition());
        assertFalse(whitePawn.isMoved());

        whitePawn.move(targetPosition);

        assertEquals(whitePawn.getPosition(), board.getPosition("e4").get());
        assertTrue(whitePawn.isMoved());
    }

    @Test
    void testPawnMoveActionValidation() {
        var board = new StandardBoard();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();
        var targetPosition = board.getPosition("f4").get();

        assertEquals(board.getPosition("e2").get(), whitePawn.getPosition());
        assertFalse(whitePawn.isMoved());

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> whitePawn.move(targetPosition)
        );

        assertEquals(thrown.getMessage(), "e2 invalid move to f4");

        assertEquals(whitePawn.getPosition(), board.getPosition("e2").get());
        assertFalse(whitePawn.isMoved());
    }

    @Test
    void testPawnCaptureAction() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e4")
                .withBlackPawn("d5")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e4").get();
        assertEquals(whitePawn.getPosition(), board.getPosition("e4").get());

        var blackPawn = (PawnPiece<Color>) board.getPiece("d5").get();
        assertEquals(blackPawn.getPosition(), board.getPosition("d5").get());
        assertTrue(blackPawn.isActive());

        whitePawn.capture(blackPawn);

        assertEquals(whitePawn.getPosition(), board.getPosition("d5").get());
        assertFalse(blackPawn.isActive());
    }

    @Test
    void testPawnCaptureActionValidation() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e4")
                .withBlackPawn("d6")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e4").get();
        assertEquals(whitePawn.getPosition(), board.getPosition("e4").get());

        var blackPawn = (PawnPiece<Color>) board.getPiece("d6").get();
        assertEquals(blackPawn.getPosition(), board.getPosition("d6").get());
        assertTrue(blackPawn.isActive());

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> whitePawn.capture(blackPawn)
        );

        assertEquals(thrown.getMessage(), "e4 invalid capture of d6");

        assertEquals(whitePawn.getPosition(), board.getPosition("e4").get());
        assertFalse(whitePawn.isMoved());
        assertTrue(blackPawn.isActive());
    }

    @Test
    void testPawnEnPassantAction() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e5")
                .withBlackPawn("d7")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e5").get();
        var blackPawn = (PawnPiece<Color>) board.getPiece("d7").get();

        var position = board.getPosition("d5").get();

        blackPawn.move(position);

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        assertEquals(blackPawn.getPosition(), position);

        var targetPosition = board.getPosition("d6").get();
        whitePawn.enpassant(blackPawn, targetPosition);

        assertEquals(whitePawn.getPosition(), targetPosition);
        assertFalse(blackPawn.isActive());
    }

    @Test
    void testPawnEnPassantActionValidation() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e5")
                .withBlackPawn("c7")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e5").get();
        var blackPawn = (PawnPiece<Color>) board.getPiece("c7").get();

        var position = board.getPosition("c5").get();

        blackPawn.move(position);
        assertEquals(blackPawn.getPosition(), position);

        var targetPosition = board.getPosition("c6").get();
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> whitePawn.enpassant(blackPawn, targetPosition)
        );

        assertEquals(thrown.getMessage(), "e5 invalid en passant of c6");

        assertEquals(whitePawn.getPosition(), board.getPosition("e5").get());
        assertEquals(blackPawn.getPosition(), board.getPosition("c5").get());

        assertTrue(blackPawn.isActive());
    }

    @Test
    void testPawnPromoteActionViaCapturing() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e7")
                .withBlackKnight("d8")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e7").get();
        assertTrue(isPawn(whitePawn));

        var blackKnight = board.getPiece("d8").get();

        var position = board.getPosition("d8").get();
        assertEquals(position, blackKnight.getPosition());

        whitePawn.promote(position, Piece.Type.QUEEN);

        assertEquals(position, whitePawn.getPosition());
        assertTrue(isQueen(whitePawn));
    }

    @Test
    void testPawnPromoteActionViaMoving() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e7")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e7").get();
        assertTrue(isPawn(whitePawn));

        var position = board.getPosition("e8").get();

        whitePawn.promote(position, Piece.Type.QUEEN);

        assertEquals(position, whitePawn.getPosition());
        assertTrue(isQueen(whitePawn));
    }

    @Test
    void testPawnPromoteActionValidation() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e6")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e6").get();
        assertTrue(isPawn(whitePawn));

        var position = board.getPosition("e8").get();

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> whitePawn.promote(position, Piece.Type.QUEEN)
        );

        assertEquals(thrown.getMessage(), "PAWN invalid promotion to QUEEN at 'e8'");
        assertTrue(isPawn(whitePawn));
    }

    @Test
    void testPawnActionAfterDisposing() {
        var board1 = new LabeledBoardBuilder()
                .withWhitePawns("a3", "b2")
                .build();

        var whitePawn = board1.getPiece("b2").get();
        assertFalse(board1.getActions(whitePawn).isEmpty());
        assertFalse(board1.getImpacts(whitePawn).isEmpty());

        ((PawnPiece<Color>) whitePawn).dispose(null);

        assertTrue(board1.getActions(whitePawn).isEmpty());
        assertTrue(board1.getImpacts(whitePawn).isEmpty());

        var board2 = new LabeledBoardBuilder()
                .withBlackPawns("b7", "a6")
                .build();

        var blackPawn = board2.getPiece("b7").get();
        assertFalse(board2.getActions(blackPawn).isEmpty());
        assertFalse(board2.getImpacts(blackPawn).isEmpty());

        ((PawnPiece<Color>) blackPawn).dispose(null);

        assertTrue(board2.getActions(blackPawn).isEmpty());
        assertTrue(board2.getImpacts(blackPawn).isEmpty());
    }

    @Test
    void testPawnPartialPinImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("e4")
                .withBlackBishop("c4")
                .withBlackPawn("b7")
                .withWhiteKing("f1")
                .withWhiteRook("e1")
                .withWhiteBishop("b2")
                .withWhitePawn("d3")
                .build();

        var whitePawn = board.getPiece("d3").get();
        var pinImpacts  = board.getImpacts(whitePawn, Impact.Type.PIN);
        assertFalse(pinImpacts.isEmpty());

        var partialPinImpacts = pinImpacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .filter(PiecePinImpact::isPartial)
                .map(impact -> (PiecePartialPinImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(partialPinImpacts.isEmpty());

        var partialPinImpact = partialPinImpacts.getFirst();
        assertTrue(partialPinImpact.isMode(PiecePinImpact.Mode.ABSOLUTE));

        var whiteKing   = board.getPiece("f1").get();
        var blackBishop = board.getPiece("c4").get();

        assertEquals(whiteKing,   partialPinImpact.getDefended());
        assertEquals(blackBishop, partialPinImpact.getAttacker());
    }

    @Test
    void testPawnRelativeForkImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("d7")
                .withBlackRook("a8")
                .withBlackPawn("g4")
                .withWhiteKing("c1")
                .withWhiteKnight("b6")
                .withWhiteRooks("f3","h3")
                .build();

        var blackPawn = board.getPiece("g4").get();

        var forkImpacts = board.getImpacts(blackPawn, Impact.Type.FORK);
        assertFalse(forkImpacts.isEmpty());

        var relativeForkImpacts = forkImpacts.stream()
                .map(impact -> (PieceForkImpact<?,?,?,?>) impact)
                .filter(PieceForkImpact::isRelative)
                .map(impact -> (PieceRelativeForkImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertEquals(1, relativeForkImpacts.size());

        var relativeForkImpact = relativeForkImpacts.getFirst();

        var forkedImpacts = new ArrayList<>(relativeForkImpact.getTarget());
        assertEquals(blackPawn, relativeForkImpact.getSource());
        assertEquals(2, forkedImpacts.size());

        var blackRook1 = board.getPiece("h3").get();
        var impact1 = forkedImpacts.getFirst();
        assertEquals(blackRook1, impact1.getTarget());
        assertEquals(blackRook1.getPosition(), impact1.getPosition());

        var blackRook2 = board.getPiece("f3").get();
        var impact2 = forkedImpacts.getLast();
        assertEquals(blackRook2, impact2.getTarget());
        assertEquals(blackRook2.getPosition(), impact2.getPosition());

        forkedImpacts.forEach(impact -> {
            assertTrue(isAttack(impact));
            assertTrue(isPawn(impact.getSource()));
            assertTrue(isRook(impact.getTarget()));
            assertTrue(impact.getLine().isEmpty());
        });
    }

    @Test
    void testPawnRelativeForkImpactWithEnPassant() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("f6")
                .withBlackPawn("d7")
                .withWhiteKing("e1")
                .withWhitePawn("e5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("d7").get();
        blackPawn.move(board.getPosition("d5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var whitePawn = board.getPiece("e5").get();

        var forkImpacts = board.getImpacts(whitePawn, Impact.Type.FORK);
        assertFalse(forkImpacts.isEmpty());

        var relativeForkImpacts = forkImpacts.stream()
                .map(impact -> (PieceForkImpact<?,?,?,?>) impact)
                .filter(PieceForkImpact::isRelative)
                .map(impact -> (PieceRelativeForkImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertEquals(1, relativeForkImpacts.size());

        var relativeForkImpact = relativeForkImpacts.getFirst();

        var forkedImpacts = new ArrayList<>(relativeForkImpact.getTarget());
        assertEquals(whitePawn, relativeForkImpact.getSource());
        assertEquals(2, forkedImpacts.size());

        var blackKnight = board.getPiece("f6").get();
        var impact1 = forkedImpacts.getFirst();
        assertEquals(blackKnight, impact1.getTarget());
        assertEquals(blackKnight.getPosition(), impact1.getPosition());

        var impact2 = forkedImpacts.getLast();
        assertEquals(blackPawn, impact2.getTarget());
        assertEquals(positionOf("d6"), impact2.getPosition());

        forkedImpacts.forEach(impact -> {
            assertTrue(isAttack(impact));
            assertTrue(isPawn(impact.getSource()));
            assertTrue(impact.getLine().isEmpty());
        });
    }

    @Test
    void testPawnDiscoveredCheckImpactWithEnPassant() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("h1")
                .withWhiteRook("h4")
                .withWhiteBishop("f4")
                .withWhitePawn("h5")
                .withBlackKing("h6")
                .withBlackPawn("g7")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("g7").get();
        blackPawn.move(board.getPosition("g5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var whitePawn = board.getPiece("h5").get();

        var discoveredAttackImpacts = board.getImpacts(whitePawn, Impact.Type.ATTACK);
        assertFalse(discoveredAttackImpacts.isEmpty());

        var absoluteDiscoveredAttackImpacts = discoveredAttackImpacts.stream()
                .map(impact -> (PieceDiscoveredAttackImpact<?,?,?,?,?>) impact)
                .filter(PieceDiscoveredAttackImpact::isAbsolute)
                .map(impact -> (PieceAbsoluteDiscoveredAttackImpact<?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(absoluteDiscoveredAttackImpacts.isEmpty());
        assertEquals(1, absoluteDiscoveredAttackImpacts.size());

        var absoluteDiscoveredAttackImpact = absoluteDiscoveredAttackImpacts.getFirst();
        assertEquals(whitePawn, absoluteDiscoveredAttackImpact.getSource());

        var blackKing = board.getPiece("h6").get();

        var checkImpact = absoluteDiscoveredAttackImpact.getTarget();
        assertEquals(blackKing, checkImpact.getTarget());
        assertTrue(checkImpact.isHidden());

        var whiteRook = board.getPiece("h4").get();
        assertEquals(whiteRook, checkImpact.getSource());

        var line = checkImpact.getLine();

        assertTrue(line.isPresent());
        assertFalse(line.get().isEmpty());
    }

    @Test
    void testPawnWithoutDiscoveredAttackImpactNoActionAvailable() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("h1")
                .withWhiteRook("h4")
                .withWhitePawn("h7")
                .withBlackKing("h8")
                .build();

        var whitePawn = board.getPiece("h7").get();

        var discoveredAttackImpacts = board.getImpacts(whitePawn, Impact.Type.ATTACK);
        assertTrue(discoveredAttackImpacts.isEmpty());
    }

    @Test
    void testPawnWithoutDiscoveredAttackImpactActionAvailableInsideLine() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("h1")
                .withWhiteRook("h4")
                .withWhitePawn("h6")
                .withBlackKing("h8")
                .build();

        var whitePawn = board.getPiece("h6").get();

        var discoveredAttackImpacts = board.getImpacts(whitePawn, Impact.Type.ATTACK);
        assertTrue(discoveredAttackImpacts.isEmpty());
    }

    @Test
    void testPawnUnderminingImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("h1")
                .withWhitePawns("a4","b3","c3")
                .withBlackKing("h8")
                .withBlackPawns("b5","c4")
                .build();

        var whitePawn1 = board.getPiece("a4").get();
        var underminingImpacts1 = new ArrayList<>(
                board.getImpacts(whitePawn1, Impact.Type.UNDERMINING)
        );

        assertFalse(underminingImpacts1.isEmpty());
        assertEquals(1, underminingImpacts1.size());

        var underminingImpact = (PieceUnderminingImpact<?,?,?,?>) underminingImpacts1.getFirst();
        assertEquals(whitePawn1, underminingImpact.getAttacker());

        var blackPawn = board.getPiece("b5").get();

        assertEquals(blackPawn, underminingImpact.getAttacked());
        assertEquals(blackPawn.getPosition(), underminingImpact.getPosition());
        assertTrue(underminingImpact.getLine().isEmpty());

        var whitePawn2 = board.getPiece("b3").get();

        var underminingImpacts2 = board.getImpacts(whitePawn2, Impact.Type.UNDERMINING);
        assertTrue(underminingImpacts2.isEmpty());
    }

    @Test
    // https://www.chess.com/terms/overloading-chess
    void testPawnOverloadingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("e8")
                .withBlackRooks("a8","f8")
                .withBlackBishop("d5")
                .withBlackKnight("f5")
                .withBlackPawns("a7","b7","c7","e6","f7","g7","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("e1")
                .withWhiteRooks("a1","f1")
                .withWhiteBishop("d3")
                .withWhiteKnight("c3")
                .withWhitePawns("a2","b2","c2","f2","g2","h2")
                .build();

        var blackPawn = board.getPiece("e6").get();
        var overloadingImpacts = board.getImpacts(blackPawn, Impact.Type.OVERLOADING);

        assertFalse(overloadingImpacts.isEmpty());
        assertEquals(2, overloadingImpacts.size());

        var expectedPositions = List.of("d5","f5");
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
    void testPawnOutpostImpactByBigMove() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g6")
                .withBlackRooks("b5","e6")
                .withBlackKnight("e8")
                .withBlackPawns("a6","b4","c5","e5","f6","g5","h6")
                .withWhiteKing("d2")
                .withWhiteRooks("a7","d8")
                .withWhiteKnight("e3")
                .withWhitePawns("a5","b3","c2","f3","g2","h3")
                .build();

        var whitePawn = board.getPiece("c2").get();
        var outpostImpacts = new ArrayList<>(
                board.getImpacts(whitePawn, Impact.Type.OUTPOST)
        );

        assertFalse(outpostImpacts.isEmpty());
        assertEquals(1, outpostImpacts.size());

        var outpostImpact = outpostImpacts.getFirst();
        assertEquals(board.getPosition("c4").get(), outpostImpact.getPosition());
    }

    @Test
    void testPawnSacrificeImpactByEnpassante() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withWhitePawn("f5")
                .withBlackKing("d6")
                .withBlackPawn("e7")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("e7").get();
        blackPawn.move(board.getPosition("e5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var whitePawn = board.getPiece("f5").get();
        var blackKing = board.getPiece("d6").get();

        var sacrificeImpacts = new ArrayList<>(
                board.getImpacts(whitePawn, Impact.Type.SACRIFICE)
        );

        assertFalse(sacrificeImpacts.isEmpty());
        assertEquals(1, sacrificeImpacts.size());

        var sacrificeImpact = sacrificeImpacts.getFirst();
        assertTrue(sacrificeImpact instanceof PieceSacrificeAttackImpact);

        var sacrificeAttackImpact = (PieceSacrificeAttackImpact<?,?,?,?,?>) sacrificeImpact;

        assertEquals(board.getPosition("e6").get(), sacrificeAttackImpact.getPosition());
        assertEquals(blackPawn, sacrificeAttackImpact.getSource().getTarget());
        assertEquals(blackKing, sacrificeAttackImpact.getAttacker());
        assertEquals(whitePawn, sacrificeAttackImpact.getSacrificed());
    }

    @Test
    // https://en.wikipedia.org/wiki/Flight_square#Luft
    void testPawnLuftImpactByMove() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackRook("e8")
                .withBlackPawn("b2")
                .withWhiteKing("h1")
                .withWhiteRook("b7")
                .withWhitePawns("h2","g2","f2")
                .build();

        var whitePawn1 = board.getPiece("h2").get();
        var luftImpacts1 = board.getImpacts(whitePawn1, Impact.Type.LUFT);

        assertFalse(luftImpacts1.isEmpty());
        assertEquals(2, luftImpacts1.size());

        var pawn1Positions = List.of(positionOf("h3"), positionOf("h4"));
        luftImpacts1.stream()
            .map(impact -> (PieceLuftImpact<?,?>) impact)
            .forEach(impact -> {
                assertTrue(pawn1Positions.contains(impact.getTarget()));
                assertEquals(whitePawn1.getPosition(), impact.getPosition());
            });

        var whitePawn2 = board.getPiece("g2").get();
        var luftImpacts2 = board.getImpacts(whitePawn2, Impact.Type.LUFT);

        assertFalse(luftImpacts2.isEmpty());
        assertEquals(2, luftImpacts2.size());

        var pawn2Positions = List.of(positionOf("g3"), positionOf("g4"));
        luftImpacts2.stream()
            .map(impact -> (PieceLuftImpact<?,?>) impact)
            .forEach(impact -> {
                assertTrue(pawn2Positions.contains(impact.getTarget()));
                assertEquals(whitePawn2.getPosition(), impact.getPosition());
            });

        var whitePawn3 = board.getPiece("f2").get();
        var luftImpacts3 = board.getImpacts(whitePawn3, Impact.Type.LUFT);

        assertTrue(luftImpacts3.isEmpty());
    }

    @Test
    void testPawnLuftImpactByCapture() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackQueen("h7")
                .withBlackBishop("f4")
                .withBlackKnights("f3","g4")
                .withWhiteKing("h1")
                .withWhitePawns("h3","g2","f2")
                .build();

        var whitePawn = board.getPiece("g2").get();
        var luftImpacts = board.getImpacts(whitePawn, Impact.Type.LUFT);

        assertFalse(luftImpacts.isEmpty());
        assertEquals(2, luftImpacts.size());

        var pawnPositions = List.of(positionOf("f3"), positionOf("g3"));
        luftImpacts.stream()
            .map(impact -> (PieceLuftImpact<?,?>) impact)
            .forEach(impact -> {
                assertTrue(pawnPositions.contains(impact.getTarget()));
                assertEquals(whitePawn.getPosition(), impact.getPosition());
            });
    }

    @Test
    void testPinnedPawnLuftImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackQueen("a8")
                .withBlackRook("h6")
                .withWhiteKing("h1")
                .withWhitePawns("h2","g2","f2")
                .build();

        var whitePawn1 = board.getPiece("g2").get();
        var luftImpacts1 = board.getImpacts(whitePawn1, Impact.Type.LUFT);

        assertTrue(luftImpacts1.isEmpty());

        var whitePawn2 = board.getPiece("h2").get();
        var luftImpacts2 = board.getImpacts(whitePawn2, Impact.Type.LUFT);

        assertFalse(luftImpacts2.isEmpty());
        assertEquals(2, luftImpacts2.size());

        var pawnPositions = List.of(positionOf("h3"), positionOf("h4"));
        luftImpacts2.stream()
            .map(impact -> (PieceLuftImpact<?,?>) impact)
            .forEach(impact -> {
                assertTrue(pawnPositions.contains(impact.getTarget()));
                assertEquals(whitePawn2.getPosition(), impact.getPosition());
            });
    }

    @Test
    void testPinnedPawnLuftImpactByCapturingAttacker() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackQueen("f3")
                .withWhiteKing("h1")
                .withWhitePawns("h2","g2","f2")
                .build();

        var whitePawn = board.getPiece("g2").get();
        var luftImpacts = board.getImpacts(whitePawn, Impact.Type.LUFT);

        assertFalse(luftImpacts.isEmpty());
        assertEquals(1, luftImpacts.size());

        var pawnPositions = List.of(positionOf("f3"));
        luftImpacts.stream()
            .map(impact -> (PieceLuftImpact<?,?>) impact)
            .forEach(impact -> {
                assertTrue(pawnPositions.contains(impact.getTarget()));
                assertEquals(whitePawn.getPosition(), impact.getPosition());
            });
    }

    @Test
    void testPawnLuftImpactWithLineProtectedKing() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackQueen("d4")
                .withWhiteKing("h1")
                .withWhiteRook("a1")
                .withWhitePawns("h2","g2","f2")
                .build();

        var whitePawn1 = board.getPiece("g2").get();
        var luftImpacts1 = board.getImpacts(whitePawn1, Impact.Type.LUFT);

        assertTrue(luftImpacts1.isEmpty());

        var whitePawn2 = board.getPiece("h2").get();
        var luftImpacts2 = board.getImpacts(whitePawn2, Impact.Type.LUFT);

        assertTrue(luftImpacts2.isEmpty());

        var whitePawn3 = board.getPiece("f2").get();
        var luftImpacts3 = board.getImpacts(whitePawn3, Impact.Type.LUFT);

        assertTrue(luftImpacts3.isEmpty());
    }

    @Test
    void testPawnLuftImpactWithProtectedKing() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackQueen("d4")
                .withWhiteKing("h1")
                .withWhiteKnight("g3")
                .withWhitePawns("h2","g2","f2")
                .build();

        var whitePawn1 = board.getPiece("g2").get();
        var luftImpacts1 = board.getImpacts(whitePawn1, Impact.Type.LUFT);

        assertTrue(luftImpacts1.isEmpty());

        var whitePawn2 = board.getPiece("h2").get();
        var luftImpacts2 = board.getImpacts(whitePawn2, Impact.Type.LUFT);

        assertFalse(luftImpacts2.isEmpty());
        assertEquals(2, luftImpacts2.size());

        var pawnPositions = List.of(positionOf("h3"), positionOf("h4"));
        luftImpacts2.stream()
            .map(impact -> (PieceLuftImpact<?,?>) impact)
            .forEach(impact -> {
                assertTrue(pawnPositions.contains(impact.getTarget()));
                assertEquals(whitePawn2.getPosition(), impact.getPosition());
            });
    }

    @Test
    void testPawnLuftImpactWithMultiAttack() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackQueen("a8")
                .withBlackRook("g7")
                .withWhiteKing("h1")
                .withWhitePawns("h2","g2","f2")
                .build();

        var whitePawn1 = board.getPiece("g2").get();
        var luftImpacts1 = board.getImpacts(whitePawn1, Impact.Type.LUFT);

        assertTrue(luftImpacts1.isEmpty());

        var whitePawn2 = board.getPiece("f2").get();
        var luftImpacts2 = board.getImpacts(whitePawn2, Impact.Type.LUFT);

        assertTrue(luftImpacts2.isEmpty());
    }

    @Test
    void testPawnLuftImpactWithAvailableKingPosition() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackRook("g7")
                .withWhiteKing("h1")
                .withWhitePawns("h3","g2","f2")
                .build();

        var whitePawn = board.getPiece("h3").get();
        var luftImpacts = board.getImpacts(whitePawn, Impact.Type.LUFT);

        assertTrue(luftImpacts.isEmpty());
    }

    @Test
    void testPawnLuftImpactWithMovedKing() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackRook("g7")
                .withWhiteKing("h2")
                .withWhitePawns("h3","g2","f2")
                .build();

        var whitePawn = board.getPiece("h3").get();
        var luftImpacts = board.getImpacts(whitePawn, Impact.Type.LUFT);

        assertTrue(luftImpacts.isEmpty());
    }

    @Test
    void testPawnAbsoluteDesperadoImpactByEnpassante() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("d7")
                .withBlackRook("f8")
                .withBlackPawns("f7","e6","b4","c5")
                .withWhiteKing("e1")
                .withWhitePawns("b3","c2","e5","f4")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("f7").get();
        blackPawn.move(board.getPosition("f5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var whitePawn = board.getPiece("e5").get();
        var desperadoImpacts = Stream.of(board.getImpacts(whitePawn, Impact.Type.DESPERADO))
                .flatMap(Collection::stream)
                .map(impact -> (PieceDesperadoImpact<?,?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(desperadoImpacts.isEmpty());
        assertEquals(2, desperadoImpacts.size());

        var blackRook = board.getPiece("f8").get();
        var blackKnight = board.getPiece("d7").get();

        var attackers = List.of(blackKnight, blackRook);
        for (var impact : desperadoImpacts) {
            assertTrue(PieceDesperadoImpact.isAbsolute(impact));
            assertTrue(attackers.contains(impact.getAttacker()));
            assertEquals(whitePawn, impact.getDesperado());
            assertEquals(blackPawn, impact.getAttacked());
        }
    }

    @Test
    void testPawnRelativeDesperadoImpactByEnpassante() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("d7")
                .withBlackPawns("f7","e6","b4","c5")
                .withWhiteKing("e1")
                .withWhitePawns("b3","c3","e5","f4")
                .build();

        var blackPawn1 = (PawnPiece<Color>) board.getPiece("f7").get();
        blackPawn1.move(board.getPosition("f5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var whitePawn = board.getPiece("e5").get();
        var desperadoImpacts = Stream.of(board.getImpacts(whitePawn, Impact.Type.DESPERADO))
                .flatMap(Collection::stream)
                .map(impact -> (PieceDesperadoImpact<?,?,?,?,?,?>) impact)
                .collect(toList());

        assertFalse(desperadoImpacts.isEmpty());
        assertEquals(2, desperadoImpacts.size());

        var relativeDesperadoImpact = Stream.of(desperadoImpacts)
                .flatMap(Collection::stream)
                .filter(impact -> PieceDesperadoImpact.isRelative(impact))
                .map(impact -> (PieceRelativeDesperadoImpact<?,?,?,?,?,?>) impact)
                .collect(toList());

        assertEquals(1, relativeDesperadoImpact.size());

        var blackKnight = board.getPiece("d7").get();
        var blackPawn2 =  board.getPiece("c5").get();

        var attackers = List.of(blackKnight, blackPawn2);
        for (var impact : relativeDesperadoImpact) {
            assertTrue(PieceDesperadoImpact.isRelative(impact));
            assertTrue(attackers.contains(impact.getAttacker()));
            assertEquals(whitePawn, impact.getDesperado());
            assertEquals(blackPawn1, impact.getAttacked());
        }
    }

    @Test
    // https://en.wikipedia.org/wiki/Isolated_pawn
    void testPawnIsolationImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("a7","c7","d6","g7","h6")
                .withWhiteKing("e1")
                .withWhitePawns("b4","b5","c4","e4","g4","h5")
                .build();

        var expectedPawns = List.of("a7","e4");
        var isolatedPawns = Stream.of(board.getPieces(PAWN_TYPE))
                .flatMap(Collection::stream)
                .map(piece -> (PawnPiece<?>) piece)
                .filter(PawnPiece::isIsolated)
                .map(PawnPiece::getPosition)
                .map(String::valueOf)
                .toList();

        assertEquals(expectedPawns.size(), isolatedPawns.size());
        assertTrue(expectedPawns.containsAll(isolatedPawns));
    }

    @Test
    // https://en.wikipedia.org/wiki/Backward_pawn
    void testPawnBackwardImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("c7","d7")
                .withWhiteKing("e1")
                .withWhitePawn("d4")
                .build();

        var blackPawn1 = (PawnPiece<Color>) board.getPiece("c7").get();
        blackPawn1.move(board.getPosition("c6").get());

        var blackPawn2 = (PawnPiece<Color>) board.getPiece("d7").get();
        blackPawn2.move(board.getPosition("d5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var expectedPawns = List.of("c6");
        var backwardedPawns = Stream.of(board.getPieces(Colors.BLACK, PAWN_TYPE))
                .flatMap(Collection::stream)
                .map(piece -> (PawnPiece<?>) piece)
                .filter(PawnPiece::isBackwarded)
                .map(PawnPiece::getPosition)
                .map(String::valueOf)
                .toList();

        assertEquals(expectedPawns.size(), backwardedPawns.size());
        assertTrue(expectedPawns.containsAll(backwardedPawns));
    }

    static void assertPawnEnPassantActions(Board board, Color color, Piece.Type type,
                                           String sourcePosition, List<String> expectedMovePositions,
                                           List<String> expectedEnPassantPositions) {

        var optionalPiece = board.getPiece(sourcePosition);
        assertTrue(optionalPiece.isPresent());

        var piece = optionalPiece.get();
        assertEquals(sourcePosition, String.valueOf(piece.getPosition()));
        assertEquals(color, piece.getColor());
        assertEquals(type, piece.getType());

        var actions = piece.getActions();
        assertEquals(expectedMovePositions.size() + expectedEnPassantPositions.size(),
                actions.size()
        );

        if (!expectedMovePositions.isEmpty()) {
            var movePositions = actions.stream()
                    .filter(Action::isMove)
                    .map(action -> (PieceMoveAction<?,?>) action)
                    .map(PieceMoveAction::getTarget)
                    .map(String::valueOf)
                    .toList();

            assertTrue(movePositions.containsAll(expectedMovePositions));
        }

        if (!expectedEnPassantPositions.isEmpty()) {
            var enPassantPositions = actions.stream()
                    .filter(Action::isEnPassant)
                    .map(action -> (PieceEnPassantAction<?,?,?,?>) action)
                    .map(PieceEnPassantAction::getPosition)
                    .map(String::valueOf)
                    .toList();

            assertTrue(enPassantPositions.containsAll(expectedEnPassantPositions));
        }
    }

    static void assertPawnPromotionActions(Board board, Color color, Piece.Type type,
                                           String sourcePosition, List<String> expectedMovePromotePositions,
                                           List<String> expectedCapturePromotePositions) {

        var optionalPiece = board.getPiece(sourcePosition);
        assertTrue(optionalPiece.isPresent());

        var piece = optionalPiece.get();
        assertEquals(sourcePosition, String.valueOf(piece.getPosition()));
        assertEquals(color, piece.getColor());
        assertEquals(type, piece.getType());

        var actions = piece.getActions();
        assertEquals(expectedMovePromotePositions.size() + expectedCapturePromotePositions.size(),
                actions.size()
        );

        if (!expectedMovePromotePositions.isEmpty()) {
            var promotePositions = actions.stream()
                    .filter(Action::isPromote)
                    .map(action -> (PiecePromoteAction<?,?>) action)
                    .map(PiecePromoteAction::getSource)
                    .map(action -> (Action<?>) action)
                    .filter(Action::isMove)
                    .map(action -> (PieceMoveAction<?,?>) action)
                    .map(PieceMoveAction::getTarget)
                    .map(String::valueOf)
                    .toList();

            assertTrue(promotePositions.containsAll(expectedMovePromotePositions));
        }

        if (!expectedCapturePromotePositions.isEmpty()) {
            var promotePositions = actions.stream()
                    .filter(Action::isPromote)
                    .map(action -> (PiecePromoteAction<?,?>) action)
                    .map(PiecePromoteAction::getSource)
                    .map(action -> (Action<?>) action)
                    .filter(Action::isCapture)
                    .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                    .map(PieceCaptureAction::getTarget)
                    .map(enemyPiece -> (Piece<?>) enemyPiece)
                    .map(Piece::getPosition)
                    .map(String::valueOf)
                    .toList();

            assertTrue(promotePositions.containsAll(expectedCapturePromotePositions));
        }
    }
}