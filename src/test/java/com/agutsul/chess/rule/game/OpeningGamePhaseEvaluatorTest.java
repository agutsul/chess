package com.agutsul.chess.rule.game;

import static com.agutsul.chess.piece.Piece.isPawn;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class OpeningGamePhaseEvaluatorTest {

    @Mock
    Board board;
    @Mock
    Journal<ActionMemento<?,?>> journal;

    @InjectMocks
    OpeningGamePhaseEvaluator evaluator;

    @Test
    void testEmptyJournal() {
        when(journal.isEmpty())
            .thenReturn(true);

        var result = evaluator.evaluate(Colors.WHITE);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.OPENING, phase.getType());
        assertEquals(Colors.WHITE, phase.getColor());
    }

    @Test
    void testEmptyJournalForSpecificColor() {
        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(0);

        var result = evaluator.evaluate(Colors.BLACK);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.OPENING, phase.getType());
        assertEquals(Colors.BLACK, phase.getColor());
    }

    @Test
    void testIfCastlingPerformed() {
        var memento = mock(ActionMemento.class);
        when(memento.getActionType())
            .thenReturn(Action.Type.CASTLING);

        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(1);
        when(journal.get(any(Color.class)))
            .thenReturn(List.of(memento));

        var result = evaluator.evaluate(Colors.BLACK);
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIfCastlingEnabled() {
        var memento = mock(ActionMemento.class);
        when(memento.getActionType())
            .thenReturn(Action.Type.MOVE);

        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(1);
        when(journal.get(any(Color.class)))
            .thenReturn(List.of(memento));

        var king = mock(KingPiece.class);
        when(king.getSides())
            .thenReturn(emptyList());

        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));

        var result = evaluator.evaluate(Colors.BLACK);
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIsMinorPiecesDeveloping() {
        var king = mock(KingPiece.class);
        when(king.getSides())
            .thenReturn(List.of(Castlingable.Side.KING));

        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));

        var knight = mock(KnightPiece.class);
        when(knight.isMoved())
            .thenReturn(true);

        var bishop = mock(BishopPiece.class);
        when(bishop.isMoved())
            .thenReturn(true);

        when(board.getPieces(any(Color.class), any(Piece.Type.class)))
            .thenAnswer(inv -> {

                var pieces = Stream.of(knight, bishop)
                        .map(piece -> (Piece<Color>) piece)
                        .toList();

                return pieces;
            });

        var memento = mock(ActionMemento.class);
        when(memento.getActionType())
            .thenReturn(Action.Type.MOVE);

        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(1);
        when(journal.get(any(Color.class)))
            .thenReturn(List.of(memento));

        var result = evaluator.evaluate(Colors.BLACK);
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testWithoutPawns() {
        var king = mock(KingPiece.class);
        when(king.getSides())
            .thenReturn(List.of(Castlingable.Side.KING));

        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));

        var knight = mock(KnightPiece.class);
        when(knight.isMoved())
            .thenReturn(true);

        var bishop = mock(BishopPiece.class);
        when(bishop.isMoved())
            .thenReturn(false);

        when(board.getPieces(any(Color.class), any(Piece.Type.class)))
            .thenAnswer(inv -> {

                var pieceType = inv.getArgument(1, Piece.Type.class);
                if (isPawn(pieceType)) {
                    return emptyList();
                }

                var pieces = Stream.of(knight, bishop)
                        .map(piece -> (Piece<Color>) piece)
                        .toList();

                return pieces;
            });

        var memento = mock(ActionMemento.class);
        when(memento.getActionType())
            .thenReturn(Action.Type.MOVE);

        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(1);
        when(journal.get(any(Color.class)))
            .thenReturn(List.of(memento));

        var result = evaluator.evaluate(Colors.BLACK);
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testWithPawnInCenter() {
        var king = mock(KingPiece.class);
        when(king.getSides())
            .thenReturn(List.of(Castlingable.Side.KING));

        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));

        var knight = mock(KnightPiece.class);
        when(knight.isMoved())
            .thenReturn(true);

        var bishop = mock(BishopPiece.class);
        when(bishop.isMoved())
            .thenReturn(false);

        var pawn = mock(PawnPiece.class);
        when(pawn.getPosition())
            .thenReturn(positionOf("e5"));

        when(board.getPieces(any(Color.class), any(Piece.Type.class)))
            .thenAnswer(inv -> {

                var pieceType = inv.getArgument(1, Piece.Type.class);
                if (isPawn(pieceType)) {
                    return List.of(pawn);
                }

                var pieces = Stream.of(knight, bishop)
                        .map(piece -> (Piece<Color>) piece)
                        .toList();

                return pieces;
            });

        var memento = mock(ActionMemento.class);
        when(memento.getActionType())
            .thenReturn(Action.Type.MOVE);

        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(1);
        when(journal.get(any(Color.class)))
            .thenReturn(List.of(memento));

        var result = evaluator.evaluate(Colors.BLACK);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.OPENING, phase.getType());
        assertEquals(Colors.BLACK, phase.getColor());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testWithPawnControllingCenter() {
        var king = mock(KingPiece.class);
        when(king.getSides())
            .thenReturn(List.of(Castlingable.Side.KING));

        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));

        var knight = mock(KnightPiece.class);
        when(knight.isMoved())
            .thenReturn(true);

        var bishop = mock(BishopPiece.class);
        when(bishop.isMoved())
            .thenReturn(false);

        var pawn = mock(PawnPiece.class);
        when(pawn.getPosition())
            .thenReturn(positionOf("c6"));

        when(board.getPieces(any(Color.class), any(Piece.Type.class)))
            .thenAnswer(inv -> {

                var pieceType = inv.getArgument(1, Piece.Type.class);
                if (isPawn(pieceType)) {
                    return List.of(pawn);
                }

                var pieces = Stream.of(knight, bishop)
                        .map(piece -> (Piece<Color>) piece)
                        .toList();

                return pieces;
            });

        when(board.getImpacts(any(PawnPiece.class), eq(Impact.Type.CONTROL)))
            .thenAnswer(inv -> {

                var piece = inv.getArgument(0, PawnPiece.class);
                var impacts = Stream.of("d5","b5")
                        .map(PositionFactory::positionOf)
                        .map(position -> new PieceControlImpact<>(piece, position))
                        .toList();

                return impacts;
            });

        var memento = mock(ActionMemento.class);
        when(memento.getActionType())
            .thenReturn(Action.Type.MOVE);

        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(1);
        when(journal.get(any(Color.class)))
            .thenReturn(List.of(memento));

        var result = evaluator.evaluate(Colors.BLACK);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.OPENING, phase.getType());
        assertEquals(Colors.BLACK, phase.getColor());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testWithPawnNotInCenterAndNotControllingCenter() {
        var king = mock(KingPiece.class);
        when(king.getSides())
            .thenReturn(List.of(Castlingable.Side.KING));

        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));

        var knight = mock(KnightPiece.class);
        when(knight.isMoved())
            .thenReturn(true);

        var bishop = mock(BishopPiece.class);
        when(bishop.isMoved())
            .thenReturn(false);

        var pawn = mock(PawnPiece.class);
        when(pawn.getPosition())
            .thenReturn(positionOf("a4"));

        when(board.getPieces(any(Color.class), any(Piece.Type.class)))
            .thenAnswer(inv -> {

                var pieceType = inv.getArgument(1, Piece.Type.class);
                if (isPawn(pieceType)) {
                    return List.of(pawn);
                }

                var pieces = Stream.of(knight, bishop)
                        .map(piece -> (Piece<Color>) piece)
                        .toList();

                return pieces;
            });

        when(board.getImpacts(any(PawnPiece.class), eq(Impact.Type.CONTROL)))
            .thenAnswer(inv -> {

                var piece = inv.getArgument(0, PawnPiece.class);
                var impacts = Stream.of("b5")
                        .map(PositionFactory::positionOf)
                        .map(position -> new PieceControlImpact<>(piece, position))
                        .toList();

                return impacts;
            });

        var memento = mock(ActionMemento.class);
        when(memento.getActionType())
            .thenReturn(Action.Type.MOVE);

        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(1);
        when(journal.get(any(Color.class)))
            .thenReturn(List.of(memento));

        var result = evaluator.evaluate(Colors.BLACK);
        assertTrue(result.isEmpty());
    }
}