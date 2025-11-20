package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static com.agutsul.chess.piece.Piece.isKing;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

// https://en.wikipedia.org/wiki/Stalemate
final class StaleMatedBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(StaleMatedBoardStateEvaluator.class);

    StaleMatedBoardStateEvaluator(Board board) {
        super(board);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' is stalemated", color);

        var attackerColor = color.invert();

        var pieces = Stream.of(board.getPieces(color))
                .flatMap(Collection::stream)
                .sorted(comparing(Piece::getType)) // make king piece the last
                .toList();

        var actions = Stream.of(pieces)
                .flatMap(Collection::stream)
                .map(piece -> {
                    var pieceActions = board.getActions(piece);
                    if (!isKing(piece)) {
                        return pieceActions;
                    }

                    var kingActions = pieceActions.stream()
                            .filter(action -> !board.isAttacked(action.getPosition(),  attackerColor))
                            .filter(action -> !board.isMonitored(action.getPosition(), attackerColor))
                            .findFirst()
                            .map(List::of)
                            .orElse(emptyList());

                    return kingActions;
                })
                .flatMap(Collection::stream)
                .findFirst();

        return Optional.ofNullable(actions.isEmpty()
                ? staleMatedBoardState(board, color)
                : null
        );
    }
}