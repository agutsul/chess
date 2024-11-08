package com.agutsul.chess.rule.board;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.color.Color;

final class CheckedBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(CheckedBoardStateEvaluator.class);

    CheckedBoardStateEvaluator(Board board) {
        super(board);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' is checked", color);

        var optional = board.getKing(color);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var king = optional.get();
        return king.isChecked()
                ? Optional.of(new CheckedBoardState(board, color))
                : Optional.empty();
    }
}