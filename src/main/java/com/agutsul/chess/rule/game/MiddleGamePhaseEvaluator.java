package com.agutsul.chess.rule.game;

import java.util.Optional;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.game.phase.MiddleGamePhase;
import com.agutsul.chess.journal.Journal;

final class MiddleGamePhaseEvaluator
        extends AbstractGamePhaseEvaluator {

    MiddleGamePhaseEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        super(board, journal);
    }

    @Override
    public Optional<GamePhase> evaluate(Color color) {
        var pieces = board.getPieces(color);
        return Optional.ofNullable(pieces.size() > MIN_PIECES
                ? new MiddleGamePhase(color)
                : null
        );
    }
}