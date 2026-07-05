package com.agutsul.chess.rule.game;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.game.phase.OpeningGamePhase;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class OpeningGamePhaseEvaluator
        extends AbstractGamePhaseEvaluator {

    OpeningGamePhaseEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        super(board, journal);
    }

    @Override
    public Optional<GamePhase> evaluate(Color color) {

        if (journal.isEmpty() || journal.size(color) == 0) {
            return Optional.of(new OpeningGamePhase(color));
        }

        if (isCastlingPerformed(color)) {
            return Optional.empty();
        }

        var isMinorPiecesMoved = Stream.of(Piece.Type.KNIGHT, Piece.Type.BISHOP)
                .map(pieceType -> board.getPieces(color, pieceType))
                .flatMap(Collection::parallelStream)
                .map(piece -> (Movable) piece)
                .allMatch(Movable::isMoved);

        if (isMinorPiecesMoved) {
            return Optional.empty();
        }

        var pawnPieces = board.getPieces(color, Piece.Type.PAWN);
        if (pawnPieces.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(isInCenter(pawnPieces) || isCenterControlled(pawnPieces)
                ? new OpeningGamePhase(color)
                : null
        );
    }

    private boolean isCastlingPerformed(Color color) {
        // search castling action in journal
        var isCastlingFound = Stream.of(journal.get(color))
                .flatMap(Collection::parallelStream)
                .map(ActionMemento::getActionType)
                .anyMatch(Action::isCastling);

        if (isCastlingFound) {
            // so, castling already performed
            return true;
        }

        var isCastlingEnabled = Stream.of(board.getKing(color))
                .flatMap(Optional::stream)
                .map(KingPiece::getSides)
                .anyMatch(not(Collection::isEmpty));

        // if there is enabled castling for the king then it has not been performed yet
        return !isCastlingEnabled;
    }
}