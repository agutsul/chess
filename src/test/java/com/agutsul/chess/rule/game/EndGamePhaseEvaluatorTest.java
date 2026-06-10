package com.agutsul.chess.rule.game;

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
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class EndGamePhaseEvaluatorTest {

    @Mock
    Board board;
    @Mock
    Journal<ActionMemento<?,?>> journal;

    @InjectMocks
    EndGamePhaseEvaluator evaluator;

    @Test
    @SuppressWarnings("unchecked")
    void testPhaseCreationWithPassedPawn() {
        var pawn = mock(PawnPiece.class);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.isPassed())
            .thenReturn(true);

        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(pawn));

        var result = evaluator.evaluate(Colors.WHITE);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.ENDGAME, phase.getType());
        assertEquals(Colors.WHITE, phase.getColor());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPhaseCreationWithKingInCenter() {
        var king = mock(KingPiece.class);
        when(king.getType())
            .thenReturn(Piece.Type.KING);
        when(king.getPosition())
            .thenReturn(positionOf("d5"));

        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(king));
        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));

        var result = evaluator.evaluate(Colors.WHITE);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.ENDGAME, phase.getType());
        assertEquals(Colors.WHITE, phase.getColor());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPhaseCreationWithKingControlCenter() {
        var king = mock(KingPiece.class);
        when(king.getType())
            .thenReturn(Piece.Type.KING);
        when(king.getPosition())
            .thenReturn(positionOf("c5"));

        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(king));
        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));
        when(board.getImpacts(any(Piece.class), eq(Impact.Type.CONTROL)))
            .thenAnswer(inv -> {
                var piece = inv.getArgument(0, KingPiece.class);

                var impacts = Stream.of("b6","c6","d6","d5","d4","c4","b4","b5")
                        .map(PositionFactory::positionOf)
                        .map(position -> new PieceControlImpact<>(piece, position))
                        .toList();

                return impacts;
            });

        var result = evaluator.evaluate(Colors.WHITE);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.ENDGAME, phase.getType());
        assertEquals(Colors.WHITE, phase.getColor());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPhaseCreationByMajorPieces() {
        var pawn = mock(PawnPiece.class);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.isPassed())
            .thenReturn(false);

        var king = mock(KingPiece.class);
        when(king.getType())
            .thenReturn(Piece.Type.KING);
        when(king.getPosition())
            .thenReturn(positionOf("c5"));

        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(pawn, king));
        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));
        when(board.getImpacts(any(Piece.class), eq(Impact.Type.CONTROL)))
            .thenReturn(emptyList());

        var result = evaluator.evaluate(Colors.WHITE);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.ENDGAME, phase.getType());
        assertEquals(Colors.WHITE, phase.getColor());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPhaseCreation() {
        var pawn = mock(PawnPiece.class);
        when(pawn.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn.isPassed())
            .thenReturn(false);

        var king = mock(KingPiece.class);
        when(king.getType())
            .thenReturn(Piece.Type.KING);
        when(king.getPosition())
            .thenReturn(positionOf("c5"));

        var queen = mock(QueenPiece.class);
        when(queen.getType())
            .thenReturn(Piece.Type.QUEEN);

        var bishop = mock(BishopPiece.class);
        when(bishop.getType())
            .thenReturn(Piece.Type.BISHOP);

        var knight = mock(KnightPiece.class);
        when(knight.getType())
            .thenReturn(Piece.Type.KNIGHT);

        var rook = mock(RookPiece.class);
        when(rook.getType())
            .thenReturn(Piece.Type.ROOK);

        when(board.getPieces(any(Color.class)))
            .thenReturn(List.of(pawn, king, queen, bishop, knight, rook));
        when(board.getKing(any(Color.class)))
            .thenReturn(Optional.of(king));
        when(board.getImpacts(any(Piece.class), eq(Impact.Type.CONTROL)))
            .thenReturn(emptyList());

        var result = evaluator.evaluate(Colors.WHITE);
        assertTrue(result.isEmpty());
    }
}