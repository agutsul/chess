package com.agutsul.chess.rule.game;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.phase.EndGamePhase;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;

final class EndGamePhaseEvaluator
        extends AbstractGamePhaseEvaluator {

    EndGamePhaseEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        super(board, journal);
    }

    @Override
    public Optional<GamePhase> evaluate(Color color) {

        var allPieces = board.getPieces(color);
        var isPassedPawn = Stream.of(allPieces)
                .flatMap(Collection::parallelStream)
                .filter(Piece::isPawn)
                .map(piece -> (PawnPiece<?>) piece)
                .anyMatch(PawnPiece::isPassed);

        if (isPassedPawn || isKingCenterActivity(color)) {
            return Optional.of(new EndGamePhase(color));
        }

        var majorPieces = Stream.of(allPieces)
                .flatMap(Collection::parallelStream)
                .filter(not(Piece::isPawn))
                .toList();

        return Optional.ofNullable(majorPieces.size() <= MIN_PIECES
                ? new EndGamePhase(color)
                : null
        );
    }

    private boolean isKingCenterActivity(Color color) {
        return Stream.of(board.getKing(color))
                .flatMap(Optional::stream)
                .map(king -> (Piece<Color>) king)
                .map(List::of)
                .anyMatch(pieces -> isInCenter(pieces) || isCenterControlled(pieces));
    }
}