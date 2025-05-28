package com.agutsul.chess.rule.board;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.BoardStateProxy;
import com.agutsul.chess.color.Color;

final class BoardStatisticStateEvaluator
        implements BoardStateEvaluator<Optional<BoardState>> {

    private static final Logger LOGGER = getLogger(BoardStatisticStateEvaluator.class);

    private final BoardStateEvaluator<Optional<BoardState>> evaluator;

    BoardStatisticStateEvaluator(BoardStateEvaluator<Optional<BoardState>> evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking '{}' statistics", color);

        var boardState = evaluator.evaluate(color);
        if (boardState.isEmpty()) {
            LOGGER.info("Checking opponent '{}' statistics when board state is empty", color);
            return wrapBoardState(evaluator.evaluate(color.invert()));
        }

        var state = boardState.get();
        if (state.isTerminal()) {
            return boardState;
        }

        LOGGER.info("Checking opponent '{}' statistics for non-terminal board state", color);

        var opponentBoardState = evaluator.evaluate(color.invert());
        if (opponentBoardState.isEmpty()) {
            return boardState;
        }

        var opponentState = opponentBoardState.get();
        if (!opponentState.isTerminal()) {
            return boardState;
        }

        return wrapBoardState(opponentBoardState);
    }

    private static Optional<BoardState> wrapBoardState(Optional<BoardState> boardState) {
        if (boardState.isEmpty()) {
            return boardState;
        }

        return Optional.of(new BoardStateProxy(boardState.get()));
    }
}