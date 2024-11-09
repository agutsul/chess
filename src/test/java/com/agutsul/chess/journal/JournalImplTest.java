package com.agutsul.chess.journal;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.action.memento.ActionMementoFactory;
import com.agutsul.chess.action.memento.CheckMatedActionMemento;
import com.agutsul.chess.action.memento.CheckedActionMemento;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class JournalImplTest {

    @Test
    void testAddMemento() {
        var memento = createMemento();

        var journal = new JournalImpl();
        assertTrue(journal.size() == 0);

        journal.add(memento);

        assertEquals(1, journal.size());
    }

    @Test
    void testRemoveMemento() {
        var memento = createMemento();

        var journal = new JournalImpl();
        journal.add(memento);

        assertEquals(1, journal.size());

        journal.remove(journal.size() - 1);

        assertTrue(journal.size() == 0);
    }

    @Test
    void testGetMemento() {
        var memento = createMemento();

        var journal = new JournalImpl();
        journal.add(memento);

        assertEquals(memento, journal.get(0));
    }

    @Test
    void testGetMementoIndexOutOfBoundsException() {
        var journal = new JournalImpl();

        var thrown = assertThrows(
                IndexOutOfBoundsException.class,
                () -> journal.get(0)
            );

        assertEquals("Index 0 out of bounds for length 0", thrown.getMessage());
    }

    @Test
    void testMoveToString() throws IOException, URISyntaxException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("e2"));

        var position = PositionFactory.INSTANCE.create("e4");
        var action = new PieceMoveAction<>(pawn, position);

        var journal = new JournalImpl();
        journal.add(ActionMementoFactory.createMemento(action));

        var fileJournalContent = readFileContent("journal_move_ply.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testCaptureToString() throws IOException, URISyntaxException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("e5"));

        var knight = mock(KnightPiece.class);
        when(knight.getColor())
            .thenReturn(Colors.BLACK);
        when(knight.getType())
            .thenReturn(Piece.Type.KNIGHT);
        when(knight.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("c6"));

        var action = new PieceCaptureAction<>(knight, pawn);
        var journal = new JournalImpl();
        journal.add(ActionMementoFactory.createMemento(action));

        var fileJournalContent = readFileContent("journal_capture_ply.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testPromoteToString() throws URISyntaxException, IOException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getColor())
            .thenReturn(Colors.WHITE);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("e7"));

        var position = PositionFactory.INSTANCE.create("e8");
        var action = new PiecePromoteAction<>(
                new PieceMoveAction<>(pawn, position),
                mock(Observable.class)
        );

        var memento = spy(ActionMementoFactory.createMemento(action));
        when(memento.getPieceType())
            .thenReturn(Piece.Type.QUEEN);

        var journal = new JournalImpl();
        journal.add(memento);

        var fileJournalContent = readFileContent("journal_promote_ply.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testCastlingToString() throws URISyntaxException, IOException {
        var whiteKing = mock(KingPiece.class);
        when(whiteKing.getColor())
            .thenReturn(Colors.WHITE);
        when(whiteKing.getType())
            .thenReturn(Piece.Type.KING);
        when(whiteKing.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("e1"));

        var whiteRook = mock(RookPiece.class);
        when(whiteRook.getColor())
            .thenReturn(Colors.WHITE);
        when(whiteRook.getType())
            .thenReturn(Piece.Type.ROOK);
        when(whiteRook.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("h1"));

        var whiteAction = new PieceCastlingAction<>("O-O",
                new CastlingMoveAction<>(whiteKing, PositionFactory.INSTANCE.create("g1")),
                new CastlingMoveAction<>(whiteRook, PositionFactory.INSTANCE.create("f1"))
        );

        var blackKing = mock(KingPiece.class);
        when(blackKing.getColor())
            .thenReturn(Colors.BLACK);
        when(blackKing.getType())
            .thenReturn(Piece.Type.KING);
        when(blackKing.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("e8"));

        var blackRook = mock(RookPiece.class);
        when(blackRook.getColor())
            .thenReturn(Colors.BLACK);
        when(blackRook.getType())
            .thenReturn(Piece.Type.ROOK);
        when(blackRook.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("a8"));

        var blackAction = new PieceCastlingAction<>("O-O-O",
                new CastlingMoveAction<>(blackKing, PositionFactory.INSTANCE.create("c8")),
                new CastlingMoveAction<>(blackRook, PositionFactory.INSTANCE.create("d8"))
        );

        var journal = new JournalImpl();
        journal.add(ActionMementoFactory.createMemento(whiteAction));
        journal.add(ActionMementoFactory.createMemento(blackAction));

        var fileJournalContent = readFileContent("journal_castling.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testEnPassantToString() throws URISyntaxException, IOException {
        var whitePawn = mock(PawnPiece.class);
        when(whitePawn.getColor())
            .thenReturn(Colors.WHITE);
        when(whitePawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(whitePawn.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("b5"));

        var blackPawn = mock(PawnPiece.class);
        when(blackPawn.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("a7"));

        var position = PositionFactory.INSTANCE.create("a6");
        var action = new PieceEnPassantAction<>(whitePawn, blackPawn, position);

        var journal = new JournalImpl();
        journal.add(ActionMementoFactory.createMemento(action));

        var fileJournalContent = readFileContent("journal_en_passant_ply.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testCheckMoveToString() throws IOException, URISyntaxException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("e2"));

        var position = PositionFactory.INSTANCE.create("e4");
        var action = new PieceMoveAction<>(pawn, position);

        var journal = new JournalImpl();
        journal.add(new CheckedActionMemento<>(ActionMementoFactory.createMemento(action)));

        var fileJournalContent = readFileContent("journal_check_ply.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testCheckMatedMoveToString() throws IOException, URISyntaxException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.getPosition())
            .thenReturn(PositionFactory.INSTANCE.create("e2"));

        var position = PositionFactory.INSTANCE.create("e4");
        var action = new PieceMoveAction<>(pawn, position);

        var journal = new JournalImpl();
        journal.add(new CheckMatedActionMemento<>(ActionMementoFactory.createMemento(action)));

        var fileJournalContent = readFileContent("journal_checkmate_ply.txt");
        var journalStr = journal.toString();

        assertEquals(fileJournalContent, journalStr);
    }

    private String readFileContent(String fileName) throws URISyntaxException, IOException {
        var resource = getClass().getClassLoader().getResource(fileName);
        var file = new File(resource.toURI());
        return readString(file.toPath());
    }

    private static ActionMemento<?,?> createMemento() {
        var board = new BoardBuilder()
                .withWhitePawn("a2")
                .build();

        var pawn = board.getPiece("a2").get();
        var actions = board.getActions(pawn);
        assertFalse(actions.isEmpty());

        var targetPosition = board.getPosition("a3").get();
        var moveAction = actions.stream()
                .filter(action -> Action.Type.MOVE.equals(action.getType()))
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        assertFalse(moveAction.isEmpty());

        var memento = ActionMementoFactory.createMemento(moveAction.get());
        assertEquals("MOVE PAWN(a2 a3)", memento.toString());

        return memento;
    }
}