package com.agutsul.chess.rule.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class MiddleGamePhaseEvaluatorTest {

    @Mock
    Board board;
    @Mock
    Journal<ActionMemento<?,?>> journal;
    @Mock
    List<Piece<Color>> pieces;

    @InjectMocks
    MiddleGamePhaseEvaluator evaluator;

    @Test
    void testPhaseCreationWhenMuchPieces() {
        when(pieces.size())
            .thenReturn(AbstractGamePhaseEvaluator.MIN_PIECES + 1);

        when(board.getPieces(any(Color.class)))
            .thenReturn(pieces);

        var result = evaluator.evaluate(Colors.WHITE);
        assertFalse(result.isEmpty());

        var phase = result.get();
        assertEquals(GamePhase.Type.MIDDLEGAME, phase.getType());
        assertEquals(Colors.WHITE, phase.getColor());
    }

    @Test
    void testPhaseCreationWhenLessPieces() {
        when(pieces.size())
            .thenReturn(AbstractGamePhaseEvaluator.MIN_PIECES);

        when(board.getPieces(any(Color.class)))
            .thenReturn(pieces);

        var result = evaluator.evaluate(Colors.WHITE);
        assertTrue(result.isEmpty());
    }
}