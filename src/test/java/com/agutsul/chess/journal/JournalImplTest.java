package com.agutsul.chess.journal;

import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.activity.action.memento.CheckMatedActionMemento;
import com.agutsul.chess.activity.action.memento.CheckedActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;

@ExtendWith(MockitoExtension.class)
public class JournalImplTest implements TestFileReader {

    @Mock
    Board board;

    @Test
    void testAddMemento() {
        var memento = mockMemento();

        var journal = new JournalImpl();
        assertTrue(journal.isEmpty());

        journal.add(memento);

        assertEquals(1, journal.size());
    }

    @Test
    void testRemoveMemento() {
        var memento = mockMemento();

        var journal = new JournalImpl();
        journal.add(memento);

        assertEquals(1, journal.size());

        journal.remove(journal.size() - 1);

        assertTrue(journal.isEmpty());
    }

    @Test
    void testGetMemento() {
        var memento = mockMemento();

        var journal = new JournalImpl();
        journal.add(memento);

        assertEquals(memento, journal.getFirst());
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
            .thenReturn(positionOf("e2"));

        var position = positionOf("e4");
        var action = new PieceMoveAction<>(pawn, position);

        var journal = new JournalImpl();
        journal.add(createMemento(board, action));

        var fileJournalContent = readFileContent("journal_move_ply.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testCaptureToString() throws IOException, URISyntaxException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getPosition())
            .thenReturn(positionOf("e5"));

        var knight = mock(KnightPiece.class);
        when(knight.getColor())
            .thenReturn(Colors.BLACK);
        when(knight.getType())
            .thenReturn(Piece.Type.KNIGHT);
        when(knight.getPosition())
            .thenReturn(positionOf("c6"));

        var action = new PieceCaptureAction<>(knight, pawn);
        var journal = new JournalImpl();
        journal.add(createMemento(board, action));

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
            .thenReturn(positionOf("e7"));

        var position = positionOf("e8");
        var action = new PiecePromoteAction<>(
                new PieceMoveAction<>(pawn, position),
                mock(Observable.class)
        );

        var memento = spy(createMemento(board, action));
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
            .thenReturn(positionOf("e1"));

        var whiteRook = mock(RookPiece.class);
        when(whiteRook.getColor())
            .thenReturn(Colors.WHITE);
        when(whiteRook.getType())
            .thenReturn(Piece.Type.ROOK);
        when(whiteRook.getPosition())
            .thenReturn(positionOf("h1"));

        var whiteAction = new PieceCastlingAction<>(Castlingable.Side.KING,
                new CastlingMoveAction<>(whiteKing, positionOf("g1")),
                new CastlingMoveAction<>(whiteRook, positionOf("f1"))
        );

        var blackKing = mock(KingPiece.class);
        when(blackKing.getColor())
            .thenReturn(Colors.BLACK);
        when(blackKing.getType())
            .thenReturn(Piece.Type.KING);
        when(blackKing.getPosition())
            .thenReturn(positionOf("e8"));

        var blackRook = mock(RookPiece.class);
        when(blackRook.getColor())
            .thenReturn(Colors.BLACK);
        when(blackRook.getType())
            .thenReturn(Piece.Type.ROOK);
        when(blackRook.getPosition())
            .thenReturn(positionOf("a8"));

        var blackAction = new PieceCastlingAction<>(Castlingable.Side.QUEEN,
                new CastlingMoveAction<>(blackKing, positionOf("c8")),
                new CastlingMoveAction<>(blackRook, positionOf("d8"))
        );

        var journal = new JournalImpl();
        journal.add(createMemento(board, whiteAction));
        journal.add(createMemento(board, blackAction));

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
            .thenReturn(positionOf("b5"));

        var blackPawn = mock(PawnPiece.class);
        when(blackPawn.getPosition())
            .thenReturn(positionOf("a7"));

        var position = positionOf("a6");
        var action = new PieceEnPassantAction<>(whitePawn, blackPawn, position);

        var journal = new JournalImpl();
        journal.add(createMemento(board, action));

        var fileJournalContent = readFileContent("journal_en_passant_ply.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testCheckMoveToString() throws IOException, URISyntaxException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.getPosition())
            .thenReturn(positionOf("e2"));

        var position = positionOf("e4");
        var action = new PieceMoveAction<>(pawn, position);

        var journal = new JournalImpl();
        journal.add(new CheckedActionMemento<>(createMemento(board, action)));

        var fileJournalContent = readFileContent("journal_check_ply.txt");
        assertEquals(fileJournalContent, journal.toString());
    }

    @Test
    void testCheckMatedMoveToString() throws IOException, URISyntaxException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.getPosition())
            .thenReturn(positionOf("e2"));

        var position = positionOf("e4");
        var action = new PieceMoveAction<>(pawn, position);

        var journal = new JournalImpl();
        journal.add(new CheckMatedActionMemento<>(createMemento(board, action)));

        var fileJournalContent = readFileContent("journal_checkmate_ply.txt");
        var journalStr = journal.toString();

        assertEquals(fileJournalContent, journalStr);
    }

    @Test
    void testGetAll() {
        var memento = mockMemento();

        var journal = new JournalImpl();
        journal.add(memento);

        var journal2 = new JournalImpl(journal);
        assertEquals(journal.size(), journal2.getAll().size());

        var memento2 = journal.getFirst();
        assertEquals(memento, memento2);
    }

    private static ActionMemento<?,?> mockMemento() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        var pawn = board.getPiece("a2").get();
        var actions = board.getActions(pawn);
        assertFalse(actions.isEmpty());

        var targetPosition = board.getPosition("a3").get();
        var moveAction = actions.stream()
                .filter(Action::isMove)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        assertFalse(moveAction.isEmpty());

        var memento = createMemento(board, moveAction.get());
        assertEquals("MOVE PAWN(a2 a3)", memento.toString());

        return memento;
    }
}