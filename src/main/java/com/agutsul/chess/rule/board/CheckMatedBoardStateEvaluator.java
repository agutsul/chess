package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static java.util.function.Predicate.not;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.checkmate.CheckMateEvaluator;
import com.agutsul.chess.rule.checkmate.CompositeCheckMateEvaluator;

// https://en.wikipedia.org/wiki/Checkmate_pattern
final class CheckMatedBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(CheckMatedBoardStateEvaluator.class);

    private final CheckMateEvaluator checkMateEvaluator;

    CheckMatedBoardStateEvaluator(Board board) {
        super(board);
        this.checkMateEvaluator = new CompositeCheckMateEvaluator(board);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' king is checkmated", color);

        var optionalKing = board.getKing(color);
        if (optionalKing.isEmpty()) {
            return Optional.empty();
        }

        var king = optionalKing.get();

        var isCheckMated = checkMateEvaluator.evaluate(king);
        king.setCheckMated(isCheckMated);

        if (!isCheckMated) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        List<BoardState> checkMatedStates = Stream.of(board.getPieces(king.getColor().invert()))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .map(piece -> board.getImpacts(piece, Impact.Type.CHECK))
                .flatMap(Collection::stream)
                .map(impact -> (PieceCheckImpact<?,?,?,?>) impact)
                .filter(impact -> Objects.equals(impact.getTarget(), king))
                .map(PieceCheckImpact::getSource)
                .map(checkMaker -> checkMatedBoardState(board, color, (Piece<Color>) checkMaker))
                .map(boardState -> (BoardState) boardState)
                .toList();

        var boardState = switch (checkMatedStates.size()) {
        case 0 -> null;
        case 1 -> checkMatedStates.getFirst();
        default -> new CompositeBoardState(checkMatedStates);
        };

        return Optional.ofNullable(boardState);
    }
}