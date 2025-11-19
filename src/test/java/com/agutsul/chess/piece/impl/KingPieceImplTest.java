package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.impact.Impact.isAttack;
import static com.agutsul.chess.piece.Piece.isKing;
import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PieceRelativeForkImpact;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;

@ExtendWith(MockitoExtension.class)
public class KingPieceImplTest extends AbstractPieceTest {

    private static final Type KING_TYPE = Piece.Type.KING;

    @Test
    void testDefaultKingActionsOnStandardBoard() {
        var expectedPositions = List.of("e1", "e8");

        var board = new StandardBoard();
        var pieces = board.getPieces(KING_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        assertPieceActions(board, Colors.WHITE, KING_TYPE, expectedPositions.get(0));
        assertPieceActions(board, Colors.BLACK, KING_TYPE, expectedPositions.get(1));
    }

    @Test
    void testDefaultKingActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteKing("e1").build();
        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("d1", "d2", "e2", "f2", "f1")
        );

        var board2 = new LabeledBoardBuilder().withBlackKing("e8").build();
        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("d8", "d7", "e7", "f7", "f8")
        );
    }

    @Test
    void testRandomKingActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteKing("e4").build();
        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e4",
                List.of("e5", "e3", "d4", "f4", "d5", "f5", "f3", "d3")
        );

        var board2 = new LabeledBoardBuilder().withBlackKing("d5").build();
        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "d5",
                List.of("d6", "d4", "c5", "e5", "c6", "e6", "e4", "c4")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withWhiteKing("e1")
                .build();

        var boardStateEvaluator1 = new BoardStateEvaluatorImpl(board1, new JournalImpl());
        boardStateEvaluator1.evaluate(Colors.WHITE);

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "f2", "f1", "d1"),
                List.of(),
                List.of("O-O", "O-O-O")
        );

        var board2 = new LabeledBoardBuilder()
                .withBlackRooks("a8", "h8")
                .withBlackKing("e8")
                .build();

        var boardStateEvaluator2 = new BoardStateEvaluatorImpl(board2, new JournalImpl());
        boardStateEvaluator2.evaluate(Colors.BLACK);

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "f7", "f8", "d8"),
                List.of(),
                List.of("O-O", "O-O-O")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoardCastlilngBlockedByPiece() {
        var board1 = new LabeledBoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withWhiteKnight("b1")
                .withWhiteKing("e1")
                .build();

        var boardStateEvaluator1 = new BoardStateEvaluatorImpl(board1, new JournalImpl());
        boardStateEvaluator1.evaluate(Colors.WHITE);

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "f2", "f1", "d1"),
                List.of(),
                List.of("O-O")
        );

        var board2 = new LabeledBoardBuilder()
                .withBlackRooks("a8", "h8")
                .withBlackKnight("b8")
                .withBlackKing("e8")
                .build();

        var boardStateEvaluator2 = new BoardStateEvaluatorImpl(board2, new JournalImpl());
        boardStateEvaluator2.evaluate(Colors.BLACK);

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "f7", "f8", "d8"),
                List.of(),
                List.of("O-O")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoardCastlilngBlockedByKingCheck() {
        var board1 = new LabeledBoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withBlackBishop("b4")
                .withWhiteKing("e1")
                .build();

        var boardStateEvaluator1 = new BoardStateEvaluatorImpl(board1, new JournalImpl());
        var boardState1 = boardStateEvaluator1.evaluate(Colors.WHITE);
        assertEquals(BoardState.Type.CHECKED, boardState1.getType());

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "f2", "f1", "d1")
        );

        var board2 = new LabeledBoardBuilder()
                .withBlackRooks("a8", "h8")
                .withWhiteBishop("g6")
                .withBlackKing("e8")
                .build();

        var boardStateEvaluator2 = new BoardStateEvaluatorImpl(board2, new JournalImpl());
        var boardState2 = boardStateEvaluator2.evaluate(Colors.BLACK);
        assertEquals(BoardState.Type.CHECKED, boardState2.getType());

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "f8", "d8")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoardCastlilngBlockedByAttackedPositionOnQueenSide() {
        var board1 = new LabeledBoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withBlackQueen("c6")
                .withWhiteKing("e1")
                .build();

        var boardStateEvaluator1 = new BoardStateEvaluatorImpl(board1, new JournalImpl());
        boardStateEvaluator1.evaluate(Colors.WHITE);

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "f2", "f1", "d1"),
                List.of(),
                List.of("O-O")
        );

        var board2 = new LabeledBoardBuilder()
                .withBlackRooks("a8", "h8")
                .withWhiteQueen("c7")
                .withBlackKing("e8")
                .build();

        var boardStateEvaluator2 = new BoardStateEvaluatorImpl(board2, new JournalImpl());
        boardStateEvaluator2.evaluate(Colors.BLACK);

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("f8"),
                List.of(),
                List.of("O-O")
        );
    }

    @Test
    void testKingCastlingActionsOnEmptyBoardCastlilngBlockedByAttackedPositionOnKingSide() {
        var board1 = new LabeledBoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withBlackQueen("f6")
                .withWhiteKing("e1")
                .build();

        var boardStateEvaluator1 = new BoardStateEvaluatorImpl(board1, new JournalImpl());
        boardStateEvaluator1.evaluate(Colors.WHITE);

        assertPieceActions(board1, Colors.WHITE, KING_TYPE, "e1",
                List.of("e2", "d2", "d1"),
                List.of(),
                List.of("O-O-O")
        );

        var board2 = new LabeledBoardBuilder()
                .withBlackRooks("a8", "h8")
                .withWhiteQueen("f3")
                .withBlackKing("e8")
                .build();

        var boardStateEvaluator2 = new BoardStateEvaluatorImpl(board2, new JournalImpl());
        boardStateEvaluator2.evaluate(Colors.BLACK);

        assertPieceActions(board2, Colors.BLACK, KING_TYPE, "e8",
                List.of("e7", "d7", "d8"),
                List.of(),
                List.of("O-O-O")
        );
    }

    @Test
    // https://en.wikipedia.org/wiki/Scholar%27s_mate
    void testKingScholarCheckmate() {
        var board = new StandardBoard();

        var whiteActionPerformedEvent = new ClearPieceDataEvent(Colors.WHITE);
        var blackActionPerformedEvent = new ClearPieceDataEvent(Colors.BLACK);

        var boardStateEvaluator = new BoardStateEvaluatorImpl(board, new JournalImpl());

        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();
        whitePawn.move(board.getPosition("e4").get());

        board.notifyObservers(whiteActionPerformedEvent);

        var blackPawn = (PawnPiece<Color>) board.getPiece("e7").get();
        blackPawn.move(board.getPosition("e5").get());

        board.notifyObservers(blackActionPerformedEvent);

        var whiteQueen = (QueenPiece<Color>) board.getPiece("d1").get();
        whiteQueen.move(board.getPosition("h5").get());

        board.notifyObservers(whiteActionPerformedEvent);

        var blackKnight1 = (KnightPiece<Color>) board.getPiece("b8").get();
        blackKnight1.move(board.getPosition("c6").get());

        board.notifyObservers(blackActionPerformedEvent);

        var whiteBishop = (BishopPiece<Color>) board.getPiece("f1").get();
        whiteBishop.move(board.getPosition("c4").get());

        board.notifyObservers(whiteActionPerformedEvent);

        var blackKnight2 = (KnightPiece<Color>) board.getPiece("g8").get();
        blackKnight2.move(board.getPosition("f6").get());

        board.notifyObservers(blackActionPerformedEvent);

        whiteQueen.capture(board.getPiece("f7").get());

        board.notifyObservers(whiteActionPerformedEvent);

        var boardState = boardStateEvaluator.evaluate(Colors.BLACK);
        assertTrue(boardState.isType(BoardState.Type.CHECK_MATED));

        var blackKing = (KingPiece<Color>) board.getPiece("e8").get();

        assertTrue(blackKing.isChecked());
        assertTrue(blackKing.isCheckMated());
    }

    @Test
    void testKingCheckMateBlockable() {
        var board = new LabeledBoardBuilder()
                .withBlackRook("g8")
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteQueen("c3")
                .build();

        var boardStateEvaluator = new BoardStateEvaluatorImpl(board, new JournalImpl());
        var boardState = boardStateEvaluator.evaluate(Colors.BLACK);

        assertEquals(BoardState.Type.CHECKED, boardState.getType());

        var blackKing = (KingPiece<Color>) board.getPiece("h8").get();

        assertTrue(blackKing.isChecked());
        assertFalse(blackKing.isCheckMated());
    }

    @Test
    void testKingCheckMovable() {
        var board = new LabeledBoardBuilder()
                .withBlackRook("e8")
                .withBlackKing("c6")
                .withWhiteBishop("h7")
                .withWhiteKing("e3")
                .build();

        var boardStateEvaluator = new BoardStateEvaluatorImpl(board, new JournalImpl());
        var boardState = boardStateEvaluator.evaluate(Colors.WHITE);

        assertEquals(BoardState.Type.CHECKED, boardState.getType());

        var whiteKing = (KingPiece<Color>) board.getPiece("e3").get();

        assertTrue(whiteKing.isChecked());
        assertFalse(whiteKing.isCheckMated());
    }

    @Test
    void testKingCastlingAction() {
        var board = new LabeledBoardBuilder()
                .withWhiteRook("h1")
                .withWhiteKing("e1")
                .build();

        var king = (KingPiece<Color>) board.getPiece("e1").get();
        var position = board.getPosition("g1").get();

        king.castling(position);

        assertEquals(king.getPosition(), position);
    }

    @Test
    void testKingCastlingActionValidation() {
        var board = new LabeledBoardBuilder()
                .withWhiteRook("h1")
                .withWhiteKing("e1")
                .build();

        var king = (KingPiece<Color>) board.getPiece("e1").get();
        var position = board.getPosition("f2").get();

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> king.castling(position)
        );

        assertEquals("Ke1 invalid castling to f2", thrown.getMessage());
    }

    @Test
    void testKingCaptureProtectedPiece() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withBlackPawns("d2","c3")
                .build();

        var pawn = board.getPiece("d2").get();

        var king = (KingPiece<Color>) board.getPiece("e1").get();
        var actions = board.getActions(king, Action.Type.CAPTURE);

        var captureAction = actions.stream()
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), pawn))
                .findFirst();

        assertTrue(captureAction.isEmpty());
    }

    @Test
    void testKingMoveOnAttackedPosition() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withBlackRook("d8")
                .build();

        var illegalPositions = List.of(
                board.getPosition("d1").get(),
                board.getPosition("d2").get()
        );

        var king = (KingPiece<Color>) board.getPiece("e1").get();
        var actions = board.getActions(king, Action.Type.MOVE);

        var illegalMovePositions = actions.stream()
                .map(action -> (PieceMoveAction<?,?>) action)
                .filter(action -> illegalPositions.contains(action.getPosition()))
                .toList();

        assertTrue(illegalMovePositions.isEmpty());
    }

    @Test
    void testKingMoveOnMonitoredPosition() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("b1")
                .withBlackRook("d1")
                .build();

        var illegalPositions = List.of(board.getPosition("a1").get());

        var king = (KingPiece<Color>) board.getPiece("b1").get();
        var actions = board.getActions(king, Action.Type.MOVE);

        var illegalMovePositions = actions.stream()
                .map(action -> (PieceMoveAction<?,?>) action)
                .filter(action -> illegalPositions.contains(action.getPosition()))
                .toList();

        assertTrue(illegalMovePositions.isEmpty());
    }

    @Test
    void testProhibitedIsPinned() {
        var kingPiece = new KingPieceImpl<Color>(mock(AbstractBoard.class),
                Colors.WHITE, "", mock(Position.class), 1);

        var thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> kingPiece.isPinned()
        );

        assertEquals("Unable to pin KING piece", thrown.getMessage());
    }

    @Test
    void testProhibitedDispose() {
        var kingPiece = new KingPieceImpl<Color>(mock(AbstractBoard.class),
                Colors.WHITE, "", mock(Position.class), 1);

        var thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> kingPiece.dispose(now())
        );

        assertTrue(Strings.CS.startsWith(thrown.getMessage(), "Unable to dispose KING piece at"));
    }

    @Test
    void testProhibitedRestore() {
        var kingPiece = new KingPieceImpl<Color>(mock(AbstractBoard.class),
                Colors.WHITE, "", mock(Position.class), 1);

        var thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> kingPiece.restore()
        );

        assertEquals("Unable to restore KING piece", thrown.getMessage());
    }

    @Test
    void testProhibitedCreateDisposedPieceState() {
        var kingPiece = new KingPieceImpl<Color>(mock(AbstractBoard.class),
                Colors.WHITE, "", mock(Position.class), 1);

        var thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> kingPiece.createDisposedPieceState(now())
        );

        assertTrue(Strings.CS.startsWith(thrown.getMessage(), "Unable to dispose KING piece at"));
    }

    @Test
    void testKingRelativeForkImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("a5")
                .withBlackRook("e4")
                .withBlackBishop("d6")
                .withBlackKnight("e6")
                .withWhiteKing("d5")
                .build();

        var whiteKing = board.getPiece("d5").get();
        var forkImpacts = board.getImpacts(whiteKing, Impact.Type.FORK);
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
        assertEquals(whiteKing, relativeForkImpact.getSource());
        assertEquals(2, forkedImpacts.size());

        var blackRook = board.getPiece("e4").get();
        var impact1 = forkedImpacts.getFirst();
        assertEquals(blackRook, impact1.getTarget());
        assertEquals(blackRook.getPosition(), impact1.getPosition());

        var blackBishop = board.getPiece("d6").get();
        var impact2 = forkedImpacts.getLast();
        assertEquals(blackBishop, impact2.getTarget());
        assertEquals(blackBishop.getPosition(), impact2.getPosition());

        forkedImpacts.forEach(impact -> {
            assertTrue(isAttack(impact));
            assertTrue(isKing(impact.getSource()));
            assertTrue(impact.getLine().isEmpty());
        });
    }

    @Test
    void testKingUnderminingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnights("f7","h7")
                .withBlackPawn("g5")
                .withWhiteKing("g6")
                .build();

        var whiteKing = board.getPiece("g6").get();
        var underminingImpacts = new ArrayList<>(board.getImpacts(whiteKing, Impact.Type.UNDERMINING));

        assertFalse(underminingImpacts.isEmpty());
        assertEquals(1, underminingImpacts.size());

        var underminingImpact = (PieceUnderminingImpact<?,?,?,?>) underminingImpacts.getFirst();
        assertEquals(whiteKing, underminingImpact.getAttacker());
        assertTrue(underminingImpact.getLine().isEmpty());

        var blackKnight = board.getPiece("h7").get();
        assertEquals(blackKnight, underminingImpact.getAttacked());
        assertEquals(blackKnight.getPosition(), underminingImpact.getPosition());
    }
}