package com.agutsul.chess.rule.board;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.StaleMatedBoardState;
import com.agutsul.chess.color.Color;

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

        var actions = new ArrayList<Action<?>>();
        for (var piece : board.getPieces(color)) {
            actions.addAll(board.getActions(piece));
        }

        if (actions.isEmpty()) {
            return Optional.of(new StaleMatedBoardState(board, color));
        }

        var allPositions = actions.stream()
                .map(Action::getPosition)
                .collect(toSet());

        var attackerPositions = board.getPieces(color.invert()).stream()
                .map(piece -> board.getActions(piece))
                .flatMap(Collection::stream)
                .map(Action::getPosition)
                .collect(toSet());

        return attackerPositions.containsAll(allPositions)
                ? Optional.of(new StaleMatedBoardState(board, color))
                : Optional.empty();
    }
}