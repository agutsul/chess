package com.agutsul.chess.rule.game;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.journal.Journal;

public final class GamePhaseEvaluatorImpl
        implements GamePhaseEvaluator<GamePhase> {

    private final Collection<GamePhaseEvaluator<Optional<GamePhase>>> evaluators;

    public GamePhaseEvaluatorImpl(Board board, Journal<ActionMemento<?,?>> journal) {
        this(List.of(
                new OpeningGamePhaseEvaluator(board, journal),
                new MiddleGamePhaseEvaluator(board, journal),
                new EndGamePhaseEvaluator(board, journal)
        ));
    }

    GamePhaseEvaluatorImpl(Collection<GamePhaseEvaluator<Optional<GamePhase>>> evaluators) {
        this.evaluators = evaluators;
    }

    @Override
    public GamePhase evaluate(Color color) {
        return Stream.of(this.evaluators)
            .flatMap(Collection::stream)
            .map(evaluator -> evaluator.evaluate(color))
            .flatMap(Optional::stream)
            .findFirst()
            .orElse(null);
    }
}