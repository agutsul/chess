package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.impact.Impact.isAttack;
import static com.agutsul.chess.piece.Piece.isPawn;
import static com.agutsul.chess.piece.Piece.isQueen;
import static com.agutsul.chess.piece.Piece.isRook;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PiecePartialPinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativeForkImpact;
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

        assertEquals(whitePawn.getPosition(), board.getPosition("e2").get());
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

        assertEquals(whitePawn.getPosition(), board.getPosition("e2").get());
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

        var forkedImpacts = relativeForkImpact.getTarget();
        assertEquals(blackPawn, relativeForkImpact.getSource());
        assertEquals(2, forkedImpacts.size());

        forkedImpacts.forEach(impact -> {
            assertTrue(isAttack(impact));
            assertEquals(blackPawn.getPosition(), impact.getPosition());

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

        assertEquals(Piece.Type.KNIGHT, forkedImpacts.getFirst().getTarget().getType());
        assertEquals(Piece.Type.PAWN, forkedImpacts.getLast().getTarget().getType());

        forkedImpacts.forEach(impact -> {
            assertTrue(isAttack(impact));
            assertEquals(whitePawn.getPosition(), impact.getPosition());
            assertTrue(isPawn(impact.getSource()));
            assertTrue(impact.getLine().isEmpty());
        });
    }

    static void assertPawnEnPassantActions(Board board, Color color, Piece.Type type,
                                           String sourcePosition, List<String> expectedMovePositions,
                                           List<String> expectedEnPassantPositions) {

        var optionalPiece = board.getPiece(sourcePosition);
        assertTrue(optionalPiece.isPresent());

        var piece = optionalPiece.get();
        assertEquals(String.valueOf(piece.getPosition()), sourcePosition);
        assertEquals(piece.getColor(), color);
        assertEquals(piece.getType(), type);

        var actions = piece.getActions();
        assertEquals(actions.size(), expectedMovePositions.size() + expectedEnPassantPositions.size());

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
        assertEquals(String.valueOf(piece.getPosition()), sourcePosition);
        assertEquals(piece.getColor(), color);
        assertEquals(piece.getType(), type);

        var actions = piece.getActions();
        assertEquals(actions.size(), expectedMovePromotePositions.size() + expectedCapturePromotePositions.size());

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