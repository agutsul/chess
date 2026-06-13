package com.agutsul.chess.rule.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.phase.EndGamePhase;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.game.phase.MiddleGamePhase;
import com.agutsul.chess.game.phase.OpeningGamePhase;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class GamePhaseEvaluatorImplTest {

    @Mock
    OpeningGamePhaseEvaluator openingGamePhaseEvaluator;
    @Mock
    MiddleGamePhaseEvaluator  middleGamePhaseEvaluator;
    @Mock
    EndGamePhaseEvaluator     endGamePhaseEvaluator;

    @Test
    void testOpeningGamePhase() {
        when(openingGamePhaseEvaluator.evaluate(any(Color.class)))
            .thenAnswer(inv -> Optional.of(new OpeningGamePhase(inv.getArgument(0, Color.class))));

        var evaluator = new GamePhaseEvaluatorImpl(List.of(
                openingGamePhaseEvaluator,
                middleGamePhaseEvaluator,
                endGamePhaseEvaluator
        ));

        var phase = evaluator.evaluate(Colors.WHITE);
        assertNotNull(phase);
        assertEquals(GamePhase.Type.OPENING, phase.getType());
    }

    @Test
    void testMiddleGamePhase() {
        when(openingGamePhaseEvaluator.evaluate(any(Color.class)))
            .thenReturn(Optional.empty());
        when(middleGamePhaseEvaluator.evaluate(any(Color.class)))
            .thenAnswer(inv -> Optional.of(new MiddleGamePhase(inv.getArgument(0, Color.class))));

        var evaluator = new GamePhaseEvaluatorImpl(List.of(
                openingGamePhaseEvaluator,
                middleGamePhaseEvaluator,
                endGamePhaseEvaluator
        ));

        var phase = evaluator.evaluate(Colors.WHITE);
        assertNotNull(phase);
        assertEquals(GamePhase.Type.MIDDLEGAME, phase.getType());
    }

    @Test
    void testEndGamePhase() {
        when(openingGamePhaseEvaluator.evaluate(any(Color.class)))
            .thenReturn(Optional.empty());
        when(middleGamePhaseEvaluator.evaluate(any(Color.class)))
            .thenReturn(Optional.empty());
        when(endGamePhaseEvaluator.evaluate(any(Color.class)))
            .thenAnswer(inv -> Optional.of(new EndGamePhase(inv.getArgument(0, Color.class))));

        var evaluator = new GamePhaseEvaluatorImpl(List.of(
                openingGamePhaseEvaluator,
                middleGamePhaseEvaluator,
                endGamePhaseEvaluator
        ));

        var phase = evaluator.evaluate(Colors.WHITE);
        assertNotNull(phase);
        assertEquals(GamePhase.Type.ENDGAME, phase.getType());
    }

    @Test
    void testGetGamePhase() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("g1")
                .withWhiteQueen("g8")
                .withWhiteRooks("a1","f1")
                .withWhiteBishops("d2","e2")
                .withWhiteKnights("f3","e4")
                .withWhitePawns("a2","b3","f2","g2","h3")
                .withBlackKing("h5")
                .withBlackPawns("a7","b6","d7","e5","f4","g6","h7")
                .build();

        var journal = mock(Journal.class);
        when(journal.isEmpty())
            .thenReturn(false);
        when(journal.size(any(Color.class)))
            .thenReturn(20);

        @SuppressWarnings("unchecked")
        var evaluator = new GamePhaseEvaluatorImpl(board, journal);
        for (var color : Colors.values()) {
            var phase = evaluator.evaluate(color);
            assertEquals(GamePhase.Type.MIDDLEGAME, phase.getType());
        }
    }
}