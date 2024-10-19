package com.agutsul.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Color;
import com.agutsul.chess.Colors;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece.Type;
import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class PawnPieceImplTest extends AbstractPieceTest {

    private static final PositionFactory POSITION_FACTORY = PositionFactory.INSTANCE;
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

        for (var color : Colors.values()) {
            for (var piece : pieces.stream()
                    .filter(piece -> Objects.equals(piece.getColor(), color))
                    .toList()) {

                var position = piece.getPosition();
                var direction = piece.getColor() == Colors.WHITE ? 1 : -1;

                var expected = Stream.of(
                            POSITION_FACTORY.createPosition(position.x(), position.y() + direction),
                            POSITION_FACTORY.createPosition(position.x(), position.y() + 2 * direction)
                        ).map(String::valueOf)
                        .toList();

                assertTrue(expectedPositions.contains(String.valueOf(position)));
                assertPieceActions(board, color, PAWN_TYPE, String.valueOf(position), expected);
            }
        }
    }

    @Test
    void testRandomPawnActionsOnEmptyBoard() {
        var board1 = new BoardBuilder().withWhitePawn("d4").build();
        assertPieceActions(board1, Colors.WHITE, PAWN_TYPE, "d4", List.of("d5"));

        var board2 = new BoardBuilder().withBlackPawn("c5").build();
        assertPieceActions(board2, Colors.BLACK, PAWN_TYPE, "c5", List.of("c4"));
    }

    @Test
    void testPawnCaptureActionOnEmptyBoard() {
        var board1 = new BoardBuilder()
                .withBlackPawn("a5")
                .withWhitePawn("b4")
                .build();

        assertPieceActions(board1, Colors.WHITE, PAWN_TYPE, "b4", List.of("b5"), List.of("a5"));

        var board2 = new BoardBuilder()
                .withWhitePawn("d5")
                .withBlackPawn("e6")
                .build();

        assertPieceActions(board2, Colors.BLACK, PAWN_TYPE, "e6", List.of("e5"), List.of("d5"));
    }

    @Test
    void testPawnMovePromotionActionOnEmptyBoard() {
        var board1 = new BoardBuilder().withWhitePawn("c7").build();
        assertPawnPromotionActions(board1, Colors.WHITE, PAWN_TYPE, "c7", List.of("c8"), List.of());

        var board2 = new BoardBuilder().withBlackPawn("b2").build();
        assertPawnPromotionActions(board2, Colors.BLACK, PAWN_TYPE, "b2", List.of("b1"), List.of());
    }

    @Test
    void testPawnCapturePromotionActionOnEmptyBoard() {
        var board1 = new BoardBuilder()
                .withWhitePawn("c7")
                .withBlackKnight("b8")
                .build();

        assertPawnPromotionActions(board1, Colors.WHITE, PAWN_TYPE, "c7",
                List.of("c8"), List.of("b8"));

        var board2 = new BoardBuilder()
                .withBlackPawn("c2")
                .withWhiteKnight("b1")
                .build();

        assertPawnPromotionActions(board2, Colors.BLACK, PAWN_TYPE, "c2",
                List.of("c1"), List.of("b1"));
    }

    @Test
    void testPawnEnPassantActionOnEmptyBoard() {
        var board1 = new BoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board1.getPiece("a7").get();
        blackPawn.move(board1.getPosition("a5").get());

        assertPawnEnPassantActions(board1, Colors.WHITE, PAWN_TYPE, "b5",
                List.of("b6"), List.of("a6"));

        var board2 = new BoardBuilder()
                .withBlackPawn("b4")
                .withWhitePawn("a2")
                .build();

        var whitePawn = (PawnPiece<Color>) board2.getPiece("a2").get();
        whitePawn.move(board2.getPosition("a4").get());

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
        var board = new BoardBuilder()
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
        var board = new BoardBuilder()
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
        var board = new BoardBuilder()
                .withWhitePawn("e5")
                .withBlackPawn("d7")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e5").get();
        var blackPawn = (PawnPiece<Color>) board.getPiece("d7").get();

        var position = board.getPosition("d5").get();

        blackPawn.move(position);
        assertEquals(blackPawn.getPosition(), position);

        var targetPosition = board.getPosition("d6").get();
        whitePawn.enpassant(blackPawn, targetPosition);

        assertEquals(whitePawn.getPosition(), targetPosition);
        assertFalse(blackPawn.isActive());
    }

    @Test
    void testPawnEnPassantActionValidation() {
        var board = new BoardBuilder()
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
        var board = new BoardBuilder()
                .withWhitePawn("e7")
                .withBlackKnight("d8")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e7").get();
        assertEquals(whitePawn.getType(), Piece.Type.PAWN);

        var blackKnight = board.getPiece("d8").get();

        var position = board.getPosition("d8").get();
        assertEquals(blackKnight.getPosition(), position);

        whitePawn.promote(position, Piece.Type.QUEEN);

        assertEquals(whitePawn.getPosition(), position);
        assertEquals(whitePawn.getType(), Piece.Type.QUEEN);
    }

    @Test
    void testPawnPromoteActionViaMoving() {
        var board = new BoardBuilder()
                .withWhitePawn("e7")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e7").get();
        assertEquals(whitePawn.getType(), Piece.Type.PAWN);

        var position = board.getPosition("e8").get();

        whitePawn.promote(position, Piece.Type.QUEEN);

        assertEquals(whitePawn.getPosition(), position);
        assertEquals(whitePawn.getType(), Piece.Type.QUEEN);
    }

    @Test
    void testPawnPromoteActionValidation() {
        var board = new BoardBuilder()
                .withWhitePawn("e6")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("e6").get();
        assertEquals(whitePawn.getType(), Piece.Type.PAWN);

        var position = board.getPosition("e8").get();

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> whitePawn.promote(position, Piece.Type.QUEEN)
        );

        assertEquals(thrown.getMessage(), "PAWN invalid promotion to QUEEN at 'e8'");
        assertEquals(whitePawn.getType(), Piece.Type.PAWN);
    }

    @Test
    void testPawnActionAfterDisposing() {
        var board1 = new BoardBuilder()
                .withWhitePawns("a3", "b2")
                .build();

        var whitePawn = board1.getPiece("b2").get();
        assertFalse(board1.getActions(whitePawn).isEmpty());
        assertFalse(board1.getImpacts(whitePawn).isEmpty());

        ((PawnPiece<Color>) whitePawn).dispose();

        assertTrue(board1.getActions(whitePawn).isEmpty());
        assertTrue(board1.getImpacts(whitePawn).isEmpty());

        var board2 = new BoardBuilder()
                .withBlackPawns("b7", "a6")
                .build();

        var blackPawn = board2.getPiece("b7").get();
        assertFalse(board2.getActions(blackPawn).isEmpty());
        assertFalse(board2.getImpacts(blackPawn).isEmpty());

        ((PawnPiece<Color>) blackPawn).dispose();

        assertTrue(board2.getActions(blackPawn).isEmpty());
        assertTrue(board2.getImpacts(blackPawn).isEmpty());
    }

    static void assertPawnEnPassantActions(
            Board board,
            Color color,
            Piece.Type type,
            String sourcePosition,
            List<String> expectedMovePositions,
            List<String> expectedEnPassantPositions) {

        var optionalPiece = board.getPiece(sourcePosition);
        assertTrue(optionalPiece.isPresent());

        var piece = optionalPiece.get();
        assertEquals(String.valueOf(piece.getPosition()), sourcePosition);
        assertEquals(piece.getColor(), color);
        assertEquals(piece.getType(), type);

        var actions = piece.getActions();
        assertEquals(actions.size(),
                expectedMovePositions.size()
                + expectedEnPassantPositions.size());

        if (!expectedMovePositions.isEmpty()) {
            var movePositions = actions.stream()
                    .filter(action -> Action.Type.MOVE.equals(action.getType()))
                    .map(action -> (PieceMoveAction<?,?>) action)
                    .map(PieceMoveAction::getTarget)
                    .map(String::valueOf)
                    .toList();

            assertTrue(movePositions.containsAll(expectedMovePositions));
        }

        if (!expectedEnPassantPositions.isEmpty()) {
            var enPassantPositions = actions.stream()
                    .filter(action -> Action.Type.EN_PASSANT.equals(action.getType()))
                    .map(action -> (PieceEnPassantAction<?,?,?,?>) action)
                    .map(PieceEnPassantAction::getPosition)
                    .map(String::valueOf)
                    .toList();

            assertTrue(enPassantPositions.containsAll(expectedEnPassantPositions));
        }
    }

    static void assertPawnPromotionActions(
            Board board,
            Color color,
            Piece.Type type,
            String sourcePosition,
            List<String> expectedMovePromotePositions,
            List<String> expectedCapturePromotePositions) {

        var optionalPiece = board.getPiece(sourcePosition);
        assertTrue(optionalPiece.isPresent());

        var piece = optionalPiece.get();
        assertEquals(String.valueOf(piece.getPosition()), sourcePosition);
        assertEquals(piece.getColor(), color);
        assertEquals(piece.getType(), type);

        var actions = piece.getActions();
        assertEquals(actions.size(),
                expectedMovePromotePositions.size()
                + expectedCapturePromotePositions.size());

        if (!expectedMovePromotePositions.isEmpty()) {
            @SuppressWarnings("rawtypes")
            var promotePositions = actions.stream()
                    .filter(action -> Action.Type.PROMOTE.equals(action.getType()))
                    .map(action -> (PiecePromoteAction) action)
                    .map(PiecePromoteAction::getSource)
                    .map(action -> (Action) action)
                    .filter(action -> Action.Type.MOVE.equals(action.getType()))
                    .map(action -> (PieceMoveAction) action)
                    .map(PieceMoveAction::getTarget)
                    .map(String::valueOf)
                    .toList();

            assertTrue(promotePositions.containsAll(expectedMovePromotePositions));
        }

        if (!expectedCapturePromotePositions.isEmpty()) {
            @SuppressWarnings("rawtypes")
            var promotePositions = actions.stream()
                    .filter(action -> Action.Type.PROMOTE.equals(action.getType()))
                    .map(action -> (PiecePromoteAction) action)
                    .map(PiecePromoteAction::getSource)
                    .map(action -> (Action) action)
                    .filter(action -> Action.Type.CAPTURE.equals(action.getType()))
                    .map(action -> (PieceCaptureAction) action)
                    .map(PieceCaptureAction::getTarget)
                    .map(enemyPiece -> (Piece) enemyPiece)
                    .map(Piece::getPosition)
                    .map(String::valueOf)
                    .toList();

            assertTrue(promotePositions.containsAll(expectedCapturePromotePositions));
        }
    }
}
