package com.agutsul.chess.rule.board;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.color.Color;

// https://en.wikipedia.org/wiki/Checkmate_pattern
final class CheckMatedBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(CheckMatedBoardStateEvaluator.class);

    CheckMatedBoardStateEvaluator(Board board) {
        super(board);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' is checkmated", color);

        var optional = board.getKing(color);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var king = optional.get();
        return king.isCheckMated()
                ? Optional.of(new CheckMatedBoardState(board, color))
                : Optional.empty();
    }
}