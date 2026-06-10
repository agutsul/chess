package com.agutsul.chess.rule.game;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

abstract class AbstractGamePhaseEvaluator
        implements GamePhaseEvaluator<Optional<GamePhase>> {

    static final int MIN_PIECES = 4;

    static final List<Position> CENTER_POSITIONS =
            Stream.of("d4","d5","e4","e5").map(PositionFactory::positionOf).toList();

    protected final Board board;
    protected final Journal<ActionMemento<?,?>> journal;

    AbstractGamePhaseEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        this.board = board;
        this.journal = journal;
    }

    boolean isInCenter(Collection<Piece<Color>> pieces) {
        return Stream.of(pieces)
                .flatMap(Collection::stream)
                .anyMatch(piece -> CENTER_POSITIONS.contains(piece.getPosition()));
    }

    boolean isCenterControlled(Collection<Piece<Color>> pieces) {
        return Stream.of(pieces)
                .flatMap(Collection::stream)
                .map(piece -> board.getImpacts(piece, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .anyMatch(impact -> CENTER_POSITIONS.contains(impact.getPosition()));
    }
}