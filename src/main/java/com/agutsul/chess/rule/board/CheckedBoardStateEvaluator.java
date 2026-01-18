package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
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

        @SuppressWarnings("unchecked")
        List<BoardState> checkedStates = Stream.of(board.getPieces(king.getColor().invert()))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .map(piece -> board.getImpacts(piece, Impact.Type.CHECK))
                .flatMap(Collection::stream)
                .map(impact -> (PieceCheckImpact<?,?,?,?>) impact)
                .filter(impact -> Objects.equals(impact.getTarget(), king))
                .map(PieceCheckImpact::getSource)
                .map(checkMaker -> checkedBoardState(board, color, (Piece<Color>) checkMaker))
                .map(boardState -> (BoardState) boardState)
                .toList();

        king.setChecked(!checkedStates.isEmpty());

        var boardState = switch (checkedStates.size()) {
        case 0 -> null;
        case 1 -> checkedStates.getFirst();
        default -> new CompositeBoardState(checkedStates);
        };

        return Optional.ofNullable(boardState);
    }
}