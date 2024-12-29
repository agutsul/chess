package com.agutsul.chess.rule.board;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;

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

        return isChecked(optionalKing.get())
                ? Optional.of(new CheckedBoardState(board, color))
                : Optional.empty();
    }

    private boolean isChecked(KingPiece<Color> king) {
        var pieces = board.getPieces(king.getColor().invert());
        var isChecked = pieces.stream()
                .map(piece -> board.getImpacts(piece, Impact.Type.CHECK))
                .flatMap(Collection::stream)
                .map(impact -> (PieceCheckImpact<?,?,?,?>) impact)
                .map(PieceCheckImpact::getTarget)
                .anyMatch(piece -> Objects.equals(piece, king));

        king.setChecked(isChecked);
        return isChecked;
    }
}