package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
import static java.util.function.Predicate.not;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

final class CheckedBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(CheckedBoardStateEvaluator.class);

    CheckedBoardStateEvaluator(Board board) {
        super(board);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' king is checked", color);

        var optionalKing = board.getKing(color);
        if (optionalKing.isEmpty()) {
            return Optional.empty();
        }

        var king = optionalKing.get();
        var isChecked = Stream.of(board.getPieces(king.getColor().invert()))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .map(piece -> board.getImpacts(piece, Impact.Type.CHECK))
                .flatMap(Collection::stream)
                .map(impact -> (PieceCheckImpact<?,?,?,?>) impact)
                .map(PieceCheckImpact::getTarget)
                .anyMatch(piece -> Objects.equals(piece, king));

        king.setChecked(isChecked);

        return Optional.ofNullable(isChecked
                ? checkedBoardState(board, color)
                : null
        );
    }
}