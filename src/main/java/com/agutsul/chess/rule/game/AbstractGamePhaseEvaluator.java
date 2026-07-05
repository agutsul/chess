package com.agutsul.chess.rule.game;

import java.util.Collection;
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

abstract class AbstractGamePhaseEvaluator
        implements GamePhaseEvaluator<Optional<GamePhase>> {

    static final int MIN_PIECES = 4;

    protected final Board board;
    protected final Journal<ActionMemento<?,?>> journal;

    AbstractGamePhaseEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        this.board = board;
        this.journal = journal;
    }

    boolean isInCenter(Collection<Piece<Color>> pieces) {
        return Stream.of(pieces)
                .flatMap(Collection::parallelStream)
                .map(Piece::getPosition)
                .anyMatch(Position::isCentral);
    }

    boolean isCenterControlled(Collection<Piece<Color>> pieces) {
        return Stream.of(pieces)
                .flatMap(Collection::parallelStream)
                .map(piece -> board.getImpacts(piece, Impact.Type.CONTROL))
                .flatMap(Collection::parallelStream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .map(Impact::getPosition)
                .anyMatch(Position::isCentral);
    }
}